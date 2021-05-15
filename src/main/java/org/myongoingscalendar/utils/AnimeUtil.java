package org.myongoingscalendar.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.myongoingscalendar.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author firs
 */
public class AnimeUtil {
    public static List<ReturnTitleGrids> sort(List<Anime> animeDay) {
        return animeDay.stream()
                .filter(anime -> !anime.day().elapsed())
                .map(Anime::day)
                .distinct()
                .map(day ->
                        new ReturnTitleGrids(
                                day,
                                animeDay.stream()
                                        .filter(e -> day.equals(e.day()))
                                        .toList()))
                .toList();
    }

    public static String ZonesManipulations(Long unixtime, ZoneId zoneId, int switcher, Locale locale) {
        Instant instant = Instant.ofEpochSecond(unixtime);
        DateTimeFormatter date_start = DateTimeFormatter.ofPattern("EEEE dd-MM-YYYY").withLocale(locale);
        DateTimeFormatter date_start_v2 = DateTimeFormatter.ofPattern("dd-MM-YY EEEE").withLocale(locale);
        DateTimeFormatter time_start = DateTimeFormatter.ofPattern("HH:mm");
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        return switch (switcher) {
            case 1 -> date_start.format(zonedDateTime);
            case 2 -> time_start.format(zonedDateTime);
            case 3 -> date_start_v2.format(zonedDateTime);
            default -> null;
        };
    }

    public static Day makeDaySupport(Long unixtime, ZoneId zoneId) {
        Comparator<ZonedDateTime> comparator = Comparator.comparing(zdt -> zdt.truncatedTo(DAYS));
        Instant instant = Instant.ofEpochSecond(unixtime);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        ZonedDateTime today = ZonedDateTime.now(zoneId);
        return new Day()
                .elapsed(comparator.compare(zonedDateTime, today) < 0)
                .today(comparator.compare(zonedDateTime, today) == 0)
                .weekend(zonedDateTime.getDayOfWeek().equals(DayOfWeek.SATURDAY) || zonedDateTime.getDayOfWeek().equals(DayOfWeek.SUNDAY));
    }

    public static List<Rating> createRatings(Object[]... args) {
        return Arrays.stream(args)
                .filter(arg -> ((BigDecimal) arg[1]).compareTo(BigDecimal.ZERO) > 0)
                .map(arg -> new Rating()
                        .dbname((RatingDB) arg[0])
                        .score((BigDecimal) arg[1]))
                .toList();
    }

    public static BigDecimal calculateWeightedAverage(Map<RatingDB, BigDecimal> map) {
        if (map.size() > 0) {
            BigDecimal num = new BigDecimal(0);
            BigDecimal denom = new BigDecimal(0);
            for (Map.Entry<RatingDB, BigDecimal> entry : map.entrySet()) {
                num = num.add(entry.getValue().multiply(entry.getKey().getWeight()));
                denom = denom.add(entry.getKey().getWeight());
            }
            return num.divide(denom, 2, RoundingMode.HALF_UP);
        } else
            return null;
    }

    public static List<Links> createLinks(Object[]... args) {
        return Arrays.stream(args)
                .filter(arg -> arg[2] != null)
                .map(arg -> new Links()
                        .name((String) arg[0])
                        .link((arg[1] != null) ? (String) arg[1] + arg[2] : (String) arg[2])
                        .image((Image) arg[3])
                )
                .toList();
    }

    public static HashMap<String, String> createHEX(JsonNode jsonNode) {
        HashMap<String, String> hex = new HashMap<>();
        if (jsonNode != null) {
            Vibrant vibrant = new ObjectMapper().convertValue(jsonNode, Vibrant.class);
            hex.put("dark", vibrant.darkMuted() != null ? vibrant.darkMuted() : vibrant.vibrant());
            hex.put("light", vibrant.lightMuted() != null ? vibrant.lightMuted() : vibrant.vibrant());
        }
        return hex;
    }

    public static List<ImagePath> createImagePaths(Boolean downloaded, Long id) {
        String basePath = "/images/anime";
        String noImagePath = "/images/noimage.svg";
        return downloaded && id != null && id > 0
                ? Arrays.asList(
                new ImagePath(MIMEType.JPG, ImageType.FULL, basePath + "/" + MIMEType.JPG.toString() + "/" + id.toString() + MIMEType.JPG.getFormat()),
                new ImagePath(MIMEType.JPG, ImageType.THUMBNAIL, basePath + "/" + MIMEType.JPG.toString() + "/thumbnails/" + id.toString() + MIMEType.JPG.getFormat()),
                new ImagePath(MIMEType.WEBP, ImageType.FULL, basePath + "/" + MIMEType.WEBP.toString() + "/" + id.toString() + MIMEType.WEBP.getFormat()),
                new ImagePath(MIMEType.WEBP, ImageType.THUMBNAIL, basePath + "/" + MIMEType.WEBP.toString() + "/thumbnails/" + id.toString() + MIMEType.WEBP.getFormat()))
                : Arrays.asList(
                new ImagePath(MIMEType.SVG, ImageType.FULL, noImagePath),
                new ImagePath(MIMEType.SVG, ImageType.THUMBNAIL, noImagePath));
    }

    public static List<ImagePath> createAvatarPaths(String filename) {
        String basePath = "/images/avatar";
        String extension = FilenameUtils.getExtension(filename).toUpperCase();
        return Arrays.asList(
                new ImagePath(MIMEType.valueOf(extension), ImageType.FULL, basePath + "/original/" + filename),
                new ImagePath(MIMEType.WEBP, ImageType.FULL, basePath + "/" + MIMEType.WEBP.toString() + "/" + FilenameUtils.getBaseName(filename) + MIMEType.WEBP.getFormat()));
    }

    public static String createDateStart(Integer firstYear, Integer firstMonth) {
        if (firstYear != null && firstMonth != null)
            return firstYear != 0 ? firstMonth >= 10 ? firstYear + "-" + firstMonth : firstYear + "-0" + firstMonth : null;
        else return null;
    }

    public static Boolean createRecommended(Map<RatingDB, BigDecimal> map) {
        BigDecimal rating = calculateWeightedAverage(map);
        return rating != null && rating.compareTo(new BigDecimal("7.2")) > -1;
    }

    public static int daysBetween(Date date1, Date date2) {
        Calendar dayOne = Calendar.getInstance();
        dayOne.setTime(date1);

        Calendar dayTwo = Calendar.getInstance();
        dayTwo.setTime(date2);

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
        }
    }

    public static List<ElasticAnime> fillWatchingStatusAndFavorite(List<ElasticAnime> elasticAnimes, List<Long> added, List<Long> dropped, List<Long> favorites) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM")
                .parseDefaulting(DAY_OF_MONTH, 1)
                .toFormatter();

        elasticAnimes.stream()
                .filter(e -> e.dateStart() != null)
                .forEach(e -> {
                    LocalDate start = LocalDate.parse(e.dateStart(), formatter);
                    long days = DAYS.between(start, LocalDate.now());
                    if (days <= 14) e.watchingStatus(WatchingStatus.NEW);
                });

        if (added.size() > 0) {
            elasticAnimes.stream()
                    .filter(e -> added.stream().anyMatch(a -> e.tid().equals(a)))
                    .forEach(e -> e.watchingStatus(e.finished() ? WatchingStatus.WATCHED : !e.started() && !e.finished() ? WatchingStatus.PLANNED : WatchingStatus.WATCHING));
        }
        if (dropped.size() > 0)
            elasticAnimes.stream()
                    .filter(e -> dropped.stream().anyMatch(a -> e.tid().equals(a)))
                    .forEach(e -> e.watchingStatus(WatchingStatus.DROPPED));

        if (favorites.size() > 0)
            elasticAnimes.stream()
                    .filter(e -> favorites.contains(e.tid()))
                    .forEach(e -> e.favorite(true));

        return elasticAnimes;
    }

    public static boolean isScoreInCorrectRange(BigDecimal score) {
        return score.compareTo(new BigDecimal(1)) >= 0 && score.compareTo(new BigDecimal(10)) <= 0;
    }

    public static BigDecimal getAvgScore(List<BigDecimal> bigDecimals, RoundingMode roundingMode) {
        if (bigDecimals.size() == 0) return null;
        BigDecimal sum = bigDecimals.stream()
                .map(Objects::requireNonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(bigDecimals.size()), roundingMode);
    }
}
