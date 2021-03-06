package org.myongoingscalendar.manipulations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.lang3.SystemUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.myongoingscalendar.entity.AnidbEntity;
import org.myongoingscalendar.entity.OngoingEntity;
import org.myongoingscalendar.entity.RatingEntity;
import org.myongoingscalendar.model.MIMEType;
import org.myongoingscalendar.service.OngoingService;
import org.myongoingscalendar.utils.AnimeUtil;
import org.myongoingscalendar.utils.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.*;
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
    @Value("${links.webp.address}")
    private String webpPath;
    @Value("${links.webp.port}")
    private String webpPort;
    private final OngoingService ongoingService;

    @Autowired
    public ParseAniDBManipulations(OngoingService ongoingService) {
        this.ongoingService = ongoingService;
    }

    @Transactional
    public void parseAniDBForCurrentOngoings() {
        parse(ongoingService.getCurrentOngoings().stream().filter(e -> e.aid() != null).toList());
    }

    @Transactional
    public void parseAniDBForAll() {
        parse(ongoingService.findByAidIsNotNull());
    }

    private void parse(List<OngoingEntity> ongoings) {
        for (OngoingEntity ongoing : ongoings) {
            try {
                org.jsoup.nodes.Document doc = Jsoup.connect(anidbPath + ongoing.aid()).userAgent("Mozilla").timeout(60000).get();
                org.jsoup.nodes.Element anime = doc.select("anime").attr("id", String.valueOf(ongoing.aid())).get(0);
                String picture = anime.select("picture").get(0).text();
                int episodeCount = Integer.parseInt(anime.select("episodecount").get(0).text());
                String url = (anime.select("url").first() != null) ? anime.select("url").get(0).text() : "Not have url";
                String description = (anime.select("description").first() != null) ? parseAndCleanDescription(anime.select("description").get(0).text()) : "Not have description";
                String titleEN = anime.select("title").get(0).text();

                org.jsoup.nodes.Element ratings = anime.select("ratings").first();

                if (ratings != null) {
                    Elements elementsPermanent = ratings.select("permanent");
                    Elements elementsTemporary = ratings.select("permanent");

                    BigDecimal permanentRating = (elementsPermanent.first() != null) ? new BigDecimal(elementsPermanent.get(0).text()).setScale(2, RoundingMode.DOWN) : new BigDecimal(0);
                    BigDecimal temporaryRating = (elementsTemporary.first() != null) ? new BigDecimal(elementsTemporary.get(0).text()).setScale(2, RoundingMode.DOWN) : new BigDecimal(0);

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
                Boolean downloaded = downloadImage(anidbImagesPath, getAnimeImagesLocationPath() + "/jpg/", ongoing.anidbEntity().picture(), ongoing.aid());
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
                                .queryParam("path", getAnimeImagesLocationPath() + "/jpg/" + ongoing.aid() + ".jpg")
                                .build()
                                .toString()
                );
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(60000);
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
        Path imagesLocationPath = Paths.get(getAnimeImagesLocationPath());
        MIMEType mimeTypeJPG = MIMEType.JPG;
        MIMEType mimeTypeWEBP = MIMEType.WEBP;
        Path pathOriginal = Paths.get(imagesLocationPath.toString(), mimeTypeJPG.toString());
        Path pathConverted = Paths.get(imagesLocationPath.toString(), mimeTypeWEBP.toString());

        List<File> original = Arrays.asList(Objects.requireNonNull(new File(pathOriginal.toUri()).listFiles((d, name) -> name.endsWith(mimeTypeJPG.getFormat()))));
        List<File> converted = Arrays.asList(Objects.requireNonNull(new File(pathConverted.toUri()).listFiles((d, name) -> name.endsWith(mimeTypeWEBP.getFormat()))));
        if (original.size() != converted.size()) {
            File[] diff = original
                    .stream()
                    .filter(elem -> converted.stream().noneMatch(c -> FilenameUtils.getBaseName(c.getName()).equals(FilenameUtils.getBaseName(elem.getName()))))
                    .toArray(File[]::new);
            Arrays.stream(diff)
                    .forEach(e -> convertImageToWebp(e, pathConverted, false));
        }
    }

    public void checkThumbnails() {
        Path imagesLocationPath = Paths.get(getAnimeImagesLocationPath());
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

    public void deleteImagesForTitle(OngoingEntity ongoing) {
        Path imagesLocationPath = Paths.get(getAnimeImagesLocationPath());
        for (MIMEType mimeType : Arrays.asList(MIMEType.JPG, MIMEType.WEBP)) {
            File fileOriginal = new File(Paths.get(imagesLocationPath.toString(), mimeType.toString(), ongoing.aid() + mimeType.getFormat()).toUri());
            File fileThumbnail = new File(Paths.get(imagesLocationPath.toString(), mimeType.toString(), "thumbnails", ongoing.aid() + mimeType.getFormat()).toUri());

            Arrays.asList(fileOriginal, fileThumbnail)
                    .forEach(file -> {
                        if (file.delete()) {
                            log.info("Image deleted: " + file.getPath());
                        } else {
                            log.error("Can't delete image: " + file.getPath());
                        }
                    });
        }
    }

    private void makeThumbnails(File file, MIMEType mimeType) throws IOException {
        List<File> parent = Arrays.asList(Objects.requireNonNull(new File(file.getParent()).listFiles((d, name) -> name.endsWith(mimeType.getFormat()))));
        List<File> child = Arrays.asList(Objects.requireNonNull(file.listFiles((d, name) -> name.endsWith(mimeType.getFormat()))));
        if (parent.size() != child.size()) {
            File[] diff = parent
                    .stream()
                    .filter(elem -> child.stream().noneMatch(c -> FilenameUtils.getBaseName(c.getName()).equals(FilenameUtils.getBaseName(elem.getName()))))
                    .toArray(File[]::new);

            if (mimeType == MIMEType.JPG) {
                Thumbnails.of(diff)
                        .size(50, 50)
                        .crop(Positions.CENTER)
                        .outputFormat(mimeType.toString())
                        .outputQuality(1.0)
                        .toFiles(new File(getAnimeImagesLocationPath() + "jpg/thumbnails"), Rename.NO_CHANGE);
            } else if (mimeType == MIMEType.WEBP) {
                Arrays.stream(diff)
                        .forEach(e -> convertImageToWebp(e, null, true));
            }
        }
    }

    @Cacheable("getAnimeImagesLocationPath")
    public String getAnimeImagesLocationPath() {
        String osPath = SystemUtils.IS_OS_WINDOWS ? windowsImagesPath : linuxImagesPath;
        return osPath + "anime/";
    }

    private Boolean downloadImage(String url, String saveTo, String picture, Long aid) {
        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL(url + picture).openConnection();
            httpConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            httpConn.setConnectTimeout(60000);
            InputStream in = new BufferedInputStream(httpConn.getInputStream());
            OutputStream out = new BufferedOutputStream(new FileOutputStream(saveTo + aid + ".jpg"));
            for (int i; (i = in.read()) != -1; ) {
                out.write(i);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException e) {
            log.error("Can't save image to disk for aid: " + aid, e);
            return false;
        }
    }

    private void convertImageToWebp(File file, Path to, Boolean thumbnail) {
        try {
            URL url = new URL(UriComponentsBuilder.fromUriString(webpPath + ":" + webpPort).build().toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setConnectTimeout(60000);

            ObjectMapper mapperObj = new ObjectMapper();
            Map<String, String> inputMap = new HashMap<>();
            inputMap.put("quality", "90");
            if (thumbnail) {
                inputMap.put("file", Paths.get(getAnimeImagesLocationPath(), MIMEType.JPG.toString(), "thumbnails", FilenameUtils.getBaseName(file.getName()) + MIMEType.JPG.getFormat()).toString());
                inputMap.put("to", Paths.get(getAnimeImagesLocationPath(), MIMEType.WEBP.toString(), "thumbnails", FilenameUtils.getBaseName(file.getName()) + MIMEType.WEBP.getFormat()).toString());
            } else {
                inputMap.put("file", file.getAbsolutePath());
                inputMap.put("to", Paths.get(to.toString(), FilenameUtils.getBaseName(file.getName()) + MIMEType.WEBP.getFormat()).toString());
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