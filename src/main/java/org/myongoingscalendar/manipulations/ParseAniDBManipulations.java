package org.myongoingscalendar.manipulations;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.myongoingscalendar.model.MIMEType;
import org.myongoingscalendar.service.OngoingService;
import org.myongoingscalendar.utils.AnimeUtil;
import org.myongoingscalendar.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                    if (ratingsEntity.isPresent() && AnimeUtil.daysBetween(ratingsEntity.get().added(), new Date()) == 0)
                        ratingsEntity.get()
                                .anidbPermanent(permanentRating)
                                .anidbTemporary(temporaryRating);
                    else ongoing.ratingEntities().add(
                            new RatingEntity()
                                    .ongoingEntity(ongoing)
                                    .anidbPermanent(permanentRating)
                                    .anidbTemporary(temporaryRating));
                }

                if (ongoing.anidbEntity() == null)
                    ongoing.anidbEntity(
                            new AnidbEntity()
                                    .ongoingEntity(ongoing)
                                    .url(url)
                                    .titleEN(titleEN)
                                    .description(description)
                                    .episodeCount(episodeCount)
                                    .picture(picture));
                else ongoing.anidbEntity()
                        .ongoingEntity(ongoing)
                        .url(url)
                        .titleEN(titleEN)
                        .description(description)
                        .episodeCount(episodeCount)
                        .picture(picture);

                ongoingService.save(ongoing);
            } catch (Exception e) {
                log.error("Error parse aniDB title " + ongoing.aid(), e);
            }
            try {
                Thread.sleep(3000);
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
                Boolean downloaded = downloadImage(anidbImagesPath, getImagesLocationPath(), ongoing.anidbEntity().picture(), ongoing.aid());
                if (downloaded) {
                    ongoing.anidbEntity().image(true);
                    ongoingService.save(ongoing);
                }
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.error("Can't download image for aid: " + ongoing.aid(), e);
            }
        }
        checkWebpImages();
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

    public void checkWebpImages() {
        Path imagesLocationPath = Paths.get(getImagesLocationPath());
        MIMEType jpg = MIMEType.JPG;
        MIMEType webp = MIMEType.WEBP;
        Path pathOriginal = Paths.get(imagesLocationPath.toString(), jpg.toString());
        Path pathConverted = Paths.get(imagesLocationPath.toString(), webp.toString());

        List<File> original = Arrays.asList(Objects.requireNonNull(new File(pathOriginal.toUri()).listFiles((d, name) -> name.endsWith(MIMEType.getFormat(jpg)))));
        List<File> converted = Arrays.asList(Objects.requireNonNull(new File(pathConverted.toUri()).listFiles((d, name) -> name.endsWith(MIMEType.getFormat(webp)))));
        if (original.size() != converted.size()) {
            File[] diff = original
                    .stream()
                    .filter(elem -> !converted.contains(elem))
                    .toArray(File[]::new);

            Arrays.stream(diff)
                    .forEach(e -> convertImageToWebp(e, pathConverted, false));
        }
    }

    public void checkThumbnails() {
        Path imagesLocationPath = Paths.get(getImagesLocationPath());
        for (MIMEType mimeType : Arrays.asList(MIMEType.JPG, MIMEType.WEBP)) {
            try {
                URI uri = Paths.get(imagesLocationPath.toString(), mimeType.toString(), "thumbnails").toUri();
                File file = new File(uri);
                if (!file.exists()) {
                    if (file.mkdir()) makeThumbnails(file, mimeType);
                    else log.error("Can't create folder: " + uri.toString());
                } else makeThumbnails(file, mimeType);
            } catch (IOException e) {
                log.error("Can't make ." + mimeType.toString() + " thumbnails", e);
            }
        }
    }

    private void makeThumbnails(File file, MIMEType mimeType) throws IOException {
        List<File> parent = Arrays.asList(Objects.requireNonNull(new File(file.getParent()).listFiles((d, name) -> name.endsWith(MIMEType.getFormat(mimeType)))));
        List<File> child = Arrays.asList(Objects.requireNonNull(file.listFiles((d, name) -> name.endsWith(MIMEType.getFormat(mimeType)))));
        if (parent.size() != child.size()) {
            File[] diff = parent
                    .stream()
                    .filter(elem -> !child.contains(elem))
                    .toArray(File[]::new);

            if (mimeType == MIMEType.JPG) {
                Thumbnails.of(diff)
                        .size(50, 50)
                        .crop(Positions.CENTER)
                        .outputFormat(mimeType.toString())
                        .outputQuality(1.0)
                        .toFiles(new File(getImagesLocationPath() + "thumbnails"), Rename.NO_CHANGE);
            } else if (mimeType == MIMEType.WEBP) {
                Arrays.stream(diff)
                        .forEach(e -> convertImageToWebp(e, null, true));
            }
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

    private Boolean downloadImage(String url, String saveTo, String picture, Long aid) {
        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL(url + picture).openConnection();
            httpConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            InputStream in = new BufferedInputStream(httpConn.getInputStream());
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

    private void convertImageToWebp(File file, Path to, Boolean thumbnail) {
        try {
            URL url = new URL(UriComponentsBuilder.fromUriString("http://localhost:8085").build().toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            ObjectMapper mapperObj = new ObjectMapper();
            Map<String, String> inputMap = new HashMap<>();
            inputMap.put("quality", "90");
            if (thumbnail) {
                inputMap.put("file", Paths.get(getImagesLocationPath(), MIMEType.JPG.toString(), "thumbnails", StringUtils.getBaseName(file.getName()) + MIMEType.getFormat(MIMEType.JPG)).toString());
                inputMap.put("to", Paths.get(getImagesLocationPath(), MIMEType.WEBP.toString(), "thumbnails", StringUtils.getBaseName(file.getName()) + MIMEType.getFormat(MIMEType.WEBP)).toString());
            } else {
                inputMap.put("file", file.getAbsolutePath());
                inputMap.put("to", Paths.get(to.toString(), StringUtils.getBaseName(file.getName()) + MIMEType.getFormat(MIMEType.WEBP)).toString());
            }

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = mapperObj.writeValueAsString(inputMap).getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (con.getResponseCode() == 200) log.info(response.toString());
                else log.error(response.toString());
            }
        } catch (IOException e) {
            log.error("Can't convert to webp: ", e);
        }
    }
}