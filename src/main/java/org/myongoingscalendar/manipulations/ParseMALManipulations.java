package org.myongoingscalendar.manipulations;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.myongoingscalendar.model.Jikan.JikanAnime;
import org.myongoingscalendar.entity.*;
import org.myongoingscalendar.model.Jikan.JikanWrapper;
import org.myongoingscalendar.service.GenreService;
import org.myongoingscalendar.service.OngoingService;
import org.myongoingscalendar.utils.AnimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ParseMALManipulations {
    @Value("${parse.jikan.path}")
    private String jikanPath;
    private final OngoingService ongoingService;
    private final GenreService genreService;

    @Autowired
    public ParseMALManipulations(OngoingService ongoingService, GenreService genreService) {
        this.ongoingService = ongoingService;
        this.genreService = genreService;
    }

    @Transactional
    public void parseMALForCurrentOngoings() {
        parse(ongoingService.getCurrentOngoings().stream().filter(e -> e.malid() != null).toList());
    }

    @Transactional
    public void parseMALForAll() {
        parse(ongoingService.findByMalidIsNotNull());
    }

    private void parse(List<OngoingEntity> ongoings) {
        for (OngoingEntity ongoing : ongoings) {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(jikanPath + ongoing.malid()).openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(60000);
                if (con.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    final JikanWrapper jikanWrapper = new ObjectMapper().readValue(Jsoup.parse(content.toString()).text(), JikanWrapper.class);
                    final JikanAnime jikanAnime = jikanWrapper.data();
                    if (jikanAnime.genres() != null) {
                        List<MalTitleGenreEntity> genresList = jikanAnime.genres().stream()
                                .map(genre -> {
                                    Optional<GenreEntity> existentGenre = genreService.findByName(genre.name());
                                    if (existentGenre.isEmpty())
                                        existentGenre = genreService.save(new GenreEntity().name(genre.name()));
                                    return new MalTitleGenreEntity().ongoingEntity(ongoing).genreEntity(existentGenre.get());
                                })
                                .toList();
                        if (genresList.size() > 0) {
                            ongoing.malTitleGenreEntities().clear();
                            ongoingService.flush();
                            ongoing.malTitleGenreEntities().addAll(genresList);
                        }
                    }

                    String description = (jikanAnime.synopsis() != null) ? parseAndCleanMALDescription(jikanAnime.synopsis()) : "Not have description";
                    String trailerUrl = (jikanAnime.trailer() != null && jikanAnime.trailer().url() != null ) ? parseAndCleanMALTrailerUrl(jikanAnime.trailer().url()) : null;

                    if (jikanAnime.score() != null) {
                        Optional<RatingEntity> ratingsEntity = ongoing.ratingEntities().stream()
                                .max(Comparator.comparing(RatingEntity::added));
                        BigDecimal score = jikanAnime.score().setScale(2, RoundingMode.DOWN);

                        if (ratingsEntity.isPresent() && AnimeUtil.daysBetween(ratingsEntity.get().added(), new Date()) == 0)
                            ratingsEntity.get().mal(score);
                        else ongoing.ratingEntities().add(
                                new RatingEntity()
                                        .ongoingEntity(ongoing)
                                        .mal(score));
                    }

                    if (ongoing.malEntity() == null)
                        ongoing.malEntity(
                                new MalEntity()
                                        .ongoingEntity(ongoing)
                                        .description(description)
                                        .trailerUrl(trailerUrl));
                    else ongoing.malEntity()
                            .ongoingEntity(ongoing)
                            .description(description)
                            .trailerUrl(trailerUrl);

                    ongoingService.save(ongoing);
                } else
                    log.error("Can't get malid " + ongoing.malid() + ", message: " + con.getResponseCode());
            } catch (IOException e) {
                log.error("Can't parse malid " + ongoing.malid() + ", message: " + e.getMessage());
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    private String parseAndCleanMALDescription(String input) {
        Pattern pattern = Pattern.compile("^([\\W+\\w+]+)\\s((\\(|\\[)?(Written|Source)[\\W+\\w+]+([)])?)$", Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(input);
        StringBuffer sb = new StringBuffer(input.length());
        while (m.find()) m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1)));
        m.appendTail(sb);
        return sb.toString();
    }

    private String parseAndCleanMALTrailerUrl(String input) {
        return input.substring(0, input.indexOf("?")).intern();
    }
}
