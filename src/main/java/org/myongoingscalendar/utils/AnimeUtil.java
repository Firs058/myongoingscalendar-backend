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
import java.util.stream.Collectors;

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
                .collect(Collectors.toList())
                .stream()
                .map(day ->
                        new ReturnTitleGrids(
                                day,
                                animeDay.stream()
                                        .filter(e -> day.equals(e.day()))
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public static String ZonesManipulations(Long unixtime, ZoneId zoneId, int switcher, Locale locale) {
        Instant instant = Instant.ofEpochSecond(unixtime);
        DateTimeFormatter date_start = DateTimeFormatter.ofPattern("EEEE dd-MM-YYYY").withLocale(locale);
        DateTimeFormatter date_start_v2 = DateTimeFormatter.ofPattern("dd-MM-YY EEEE").withLocale(locale);
        DateTimeFormatter time_start = DateTimeFormatter.ofPattern("HH:mm");
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        switch (switcher) {
            case 1:
                return date_start.format(zonedDateTime);
            case 2:
                return time_start.format(zonedDateTime);
            case 3:
                return date_start_v2.format(zonedDateTime);
            default:
                return null;
        }
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
                .filter(arg -> (Double) arg[1] > 0)
                .map(arg -> new Rating()
                        .dbname((RatingDB) arg[0])
                        .score((Double) arg[1]))
                .collect(Collectors.toList());
    }

    public static Double calculateWeightedAverage(Map<Double, Double> map) throws ArithmeticException {
        double num = 0;
        double denom = 0;
        for (Map.Entry<Double, Double> entry : map.entrySet()) {
            num += entry.getValue() * entry.getKey();
            denom += entry.getKey();
        }
        double weightedAverage = num / denom;
        return !Double.isNaN(weightedAverage) ? BigDecimal.valueOf(weightedAverage).setScale(2, RoundingMode.HALF_UP).doubleValue() : null;
    }

    public static List<Links> createLinks(Object[]... args) {
        return Arrays.stream(args)
                .filter(arg -> arg[2] != null)
                .map(arg -> new Links()
                        .name((String) arg[0])
                        .link((arg[1] != null) ? (String) arg[1] + arg[2] : (String) arg[2])
                        .image((Image) arg[3])
                )
                .collect(Collectors.toList());
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
                new ImagePath(MIMEType.JPG, ImageType.FULL, basePath + "/" + MIMEType.JPG.toString() + "/" + id.toString() + MIMEType.getFormat(MIMEType.JPG)),
                new ImagePath(MIMEType.JPG, ImageType.THUMBNAIL, basePath + "/" + MIMEType.JPG.toString() + "/thumbnails/" + id.toString() + MIMEType.getFormat(MIMEType.JPG)),
                new ImagePath(MIMEType.WEBP, ImageType.FULL, basePath + "/" + MIMEType.WEBP.toString() + "/" + id.toString() + MIMEType.getFormat(MIMEType.WEBP)),
                new ImagePath(MIMEType.WEBP, ImageType.THUMBNAIL, basePath + "/" + MIMEType.WEBP.toString() + "/thumbnails/" + id.toString() + MIMEType.getFormat(MIMEType.WEBP)))
                : Arrays.asList(
                new ImagePath(MIMEType.SVG, ImageType.FULL, noImagePath),
                new ImagePath(MIMEType.SVG, ImageType.THUMBNAIL, noImagePath));
    }

    public static String createDateStart(Integer firstYear, Integer firstMonth) {
        if (firstYear != null && firstMonth != null)
            return firstYear != 0 ? firstMonth >= 10 ? firstYear + "-" + firstMonth : firstYear + "-0" + firstMonth : null;
        else return null;
    }

    public static Boolean createRecommended(Map<Double, Double> map) {
        Double rating = calculateWeightedAverage(map);
        return rating != null && rating >= 7.3;
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

    public static List<ElasticAnime> createWatchingStatus(List<ElasticAnime> elasticAnimes, List<Long> added, List<Long> dropped) {
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
                    .forEach(e -> e.watchingStatus(WatchingStatus.WATCHING));
            elasticAnimes.stream()
                    .filter(e -> added.stream().anyMatch(a -> e.tid().equals(a) && e.outdated()))
                    .forEach(e -> e.watchingStatus(WatchingStatus.WATCHED));
        }
        if (dropped.size() > 0)
            elasticAnimes.stream()
                    .filter(e -> dropped.stream().anyMatch(a -> e.tid().equals(a)))
                    .forEach(e -> e.watchingStatus(WatchingStatus.DROPPED));

        return elasticAnimes;
    }
}
