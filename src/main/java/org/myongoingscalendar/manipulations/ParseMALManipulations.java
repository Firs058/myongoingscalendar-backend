package org.myongoingscalendar.manipulations;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.myongoingscalendar.model.Jikan.JikanAnime;
import org.myongoingscalendar.entity.*;
import org.myongoingscalendar.service.GenreService;
import org.myongoingscalendar.service.OngoingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        parse(ongoingService.getCurrentOngoings().stream().filter(e -> e.malid() != null).collect(Collectors.toList()));
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
                if (con.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    final JikanAnime jikanAnime = new ObjectMapper().readValue(Jsoup.parse(content.toString()).text(), JikanAnime.class);
                    List<MalTitleGenreEntity> genresList = jikanAnime.genre().stream()
                            .map(genre -> {
                                Optional<GenreEntity> existentGenre = genreService.findByName(genre.name());
                                if (!existentGenre.isPresent())
                                    existentGenre = genreService.save(new GenreEntity().name(genre.name()));
                                return new MalTitleGenreEntity().ongoingEntity(ongoing).genreEntity(existentGenre.get());
                            })
                            .collect(Collectors.toList());
                    if (genresList.size() > 0) {
                        ongoing.malTitleGenreEntities().clear();
                        ongoingService.flush();
                        ongoing.malTitleGenreEntities().addAll(genresList);
                    }

                    String description = (jikanAnime.synopsis() != null) ? parseAndCleanMALDescription(jikanAnime.synopsis()) : "Not have description";
                    String trailerUrl = (jikanAnime.trailerUrl() != null) ? parseAndCleanMALTrailerUrl(jikanAnime.trailerUrl()) : null;

                    if (jikanAnime.score() != null) {
                        Optional<RatingEntity> ratingsEntity = ongoing.ratingEntities().stream()
                                .max(Comparator.comparing(RatingEntity::added));

                        if (ratingsEntity.isPresent() && Duration.between(ratingsEntity.get().added().toInstant(), Instant.now()).toDays() <= 1)
                            ratingsEntity.get().mal(jikanAnime.score());
                        else if (!ratingsEntity.isPresent() || Duration.between(ratingsEntity.get().added().toInstant(), Instant.now()).toDays() > 1)
                            ongoing.ratingEntities().add(
                                    new RatingEntity()
                                            .ongoingEntity(ongoing)
                                            .mal(jikanAnime.score()));
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
                Thread.sleep(3000);
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
        return input.substring(0, input.indexOf("?"));
    }
}
