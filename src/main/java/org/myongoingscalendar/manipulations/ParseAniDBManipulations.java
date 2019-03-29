package org.myongoingscalendar.manipulations;

import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.lang3.SystemUtils;
import org.jsoup.Jsoup;
import org.myongoingscalendar.entity.AnidbEntity;
import org.myongoingscalendar.entity.OngoingEntity;
import org.myongoingscalendar.entity.RatingEntity;
import org.myongoingscalendar.service.OngoingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ParseAniDBManipulations {
    @Value("${images.path.windows}")
    private String windowsImagesPath;
    @Value("${images.path.linux}")
    private String linuxImagesPath;
    @Value("${parse.anidb.path}")
    private String anidbPath;
    @Value("${parse.anidb.path.images}")
    private String anidbImagesPath;
    @Value("${links.vibrant.address}")
    private String vibrantPath;
    @Value("${links.vibrant.port}")
    private String vibrantPort;
    private final OngoingService ongoingService;

    @Autowired
    public ParseAniDBManipulations(OngoingService ongoingService) {
        this.ongoingService = ongoingService;
    }

    @Transactional
    public void parseAniDBForCurrentOngoings() {
        parse(ongoingService.getCurrentOngoings().stream().filter(e -> e.aid() != null).collect(Collectors.toList()));
    }

    @Transactional
    public void parseAniDBForAll() {
        parse(ongoingService.findByAidIsNotNull());
    }

    private void parse(List<OngoingEntity> ongoings) {
        for (OngoingEntity ongoing : ongoings) {
            try {
                org.jsoup.nodes.Document doc = Jsoup.connect(anidbPath + ongoing.aid()).userAgent("Mozilla").get();
                org.jsoup.nodes.Element anime = doc.select("anime").attr("id", String.valueOf(ongoing.aid())).get(0);
                String picture = anime.select("picture").get(0).text();
                int episodeCount = Integer.parseInt(anime.select("episodecount").get(0).text());
                String url = (anime.select("url").first() != null) ? anime.select("url").get(0).text() : "Not have url";
                String description = (anime.select("description").first() != null) ? parseAndCleanDescription(anime.select("description").get(0).text()) : "Not have description";
                String titleEN = anime.select("title").get(0).text();

                org.jsoup.nodes.Element ratings = anime.select("ratings").first();

                if (ratings != null) {
                    double permanentRating = (ratings.select("permanent").first() != null) ? Double.parseDouble(ratings.select("permanent").get(0).text()) : 0.0;
                    double temporaryRating = (ratings.select("temporary").first() != null) ? Double.parseDouble(ratings.select("temporary").get(0).text()) : 0.0;

                    Optional<RatingEntity> ratingsEntity = ongoing.ratingEntities().stream()
                            .max(Comparator.comparing(RatingEntity::added));
                    if (ratingsEntity.isPresent() && Duration.between(ratingsEntity.get().added().toInstant(), Instant.now()).toDays() <= 1)
                        ratingsEntity.get()
                                .anidbPermanent(permanentRating)
                                .anidbTemporary(temporaryRating);
                    else if (!ratingsEntity.isPresent() || Duration.between(ratingsEntity.get().added().toInstant(), Instant.now()).toDays() > 1)
                        ongoing.ratingEntities().add(
                                new RatingEntity()
                                        .ongoingEntity(ongoing)
                                        .anidbPermanent(permanentRating)
                                        .anidbTemporary(temporaryRating));
                }

                ongoing.anidbEntity(
                        new AnidbEntity()
                                .ongoingEntity(ongoing)
                                .url(url)
                                .titleEN(titleEN)
                                .description(description)
                                .episodeCount(episodeCount)
                                .picture(picture));

                ongoingService.save(ongoing);
            } catch (Exception e) {
                log.error("Error parse aniDB title " + ongoing.aid(), e);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }
    }

    private String parseAndCleanDescription(String input) {
        Pattern pattern = Pattern.compile("([-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*?\\s[\\[]([\\w+\\s+\\\"'`]+)[\\]])");
        Matcher m = pattern.matcher(input);
        StringBuffer sb = new StringBuffer(input.length());
        while (m.find()) m.appendReplacement(sb, Matcher.quoteReplacement(m.group(2)));
        m.appendTail(sb);
        return sb.toString();
    }

    public void getAniDBImages() {
        List<OngoingEntity> ongoings = ongoingService.getCurrentOngoingsWithoutImage();
        for (OngoingEntity ongoing : ongoings) {
            try {
                Boolean download = downloadImages(anidbImagesPath, getImagesLocationPath(), ongoing.anidbEntity().picture(), ongoing.aid());
                if (download) {
                    ongoing.anidbEntity().image(true);
                    ongoingService.save(ongoing);
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error("Can't download image for aid: " + ongoing.aid(), e);
            }
        }
        checkThumbnails();
        findHEXAndFillTable();
    }

    public void findHEXAndFillTable() {
        List<OngoingEntity> ongoings = ongoingService.getCurrentOngoingsWithoutVibrant();
        for (OngoingEntity ongoing : ongoings) {
            try {
                URL url = new URL(
                        UriComponentsBuilder
                                .fromUriString(vibrantPath + ":" + vibrantPort)
                                .queryParam("path", getImagesLocationPath() + ongoing.aid() + ".jpg")
                                .build()
                                .toString()
                );
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                if (con.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                    ongoing.anidbEntity().vibrant(JacksonUtil.toJsonNode(content.toString()));
                    ongoingService.save(ongoing);
                } else
                    log.error("Response code not 200 for aid: " + ongoing.aid() + ", message: " + con.getResponseMessage());
            } catch (IOException e) {
                log.error("Can't get hex for aid: " + ongoing.aid(), e);
            }
        }
    }

    public void checkThumbnails() {
        try {
            File path = new File(getImagesLocationPath() + "thumbnails");
            if (!path.exists()) {
                if (path.mkdir()) makeThumbnails(path);
                else log.error("Can't create folder: " + path.toString());
            } else makeThumbnails(path);
        } catch (IOException e) {
            log.error("Can't make thumbnails", e);
        }
    }

    private void makeThumbnails(File path) throws IOException {
        List<File> parent = Arrays.asList(Objects.requireNonNull(new File(path.getParent()).listFiles((d, name) -> name.endsWith(".jpg"))));
        List<File> child = Arrays.asList(Objects.requireNonNull(path.listFiles((d, name) -> name.endsWith(".jpg"))));
        if (parent.size() != child.size()) {
            File[] diff = parent
                    .stream()
                    .filter(elem -> !child.contains(elem))
                    .toArray(File[]::new);

            Thumbnails.of(diff)
                    .size(50, 50)
                    .crop(Positions.CENTER)
                    .outputFormat("jpg")
                    .outputQuality(1.0)
                    .toFiles(new File(getImagesLocationPath() + "thumbnails"), Rename.NO_CHANGE);
        }
    }

    @Cacheable("getImagesLocationPath")
    public String getImagesLocationPath() {
        return SystemUtils.IS_OS_WINDOWS
                ? windowsImagesPath
                : SystemUtils.IS_OS_LINUX
                ? linuxImagesPath
                : null;
    }

    private Boolean downloadImages(String url, String saveTo, String picture, Long aid) {
        try {
            InputStream in = new BufferedInputStream(new URL(url + picture).openStream());
            OutputStream out = new BufferedOutputStream(new FileOutputStream(saveTo + aid + ".jpg"));
            for (int i; (i = in.read()) != -1; ) {
                out.write(i);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}