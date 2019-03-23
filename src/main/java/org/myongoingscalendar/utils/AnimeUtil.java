package org.myongoingscalendar.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.myongoingscalendar.model.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author firs
 */
public class AnimeUtil {
    public static List<ReturnTitleGrids> sort(List<Anime> animeDay) {
        return animeDay.stream()
                .filter(anime -> !anime.elapsed())
                .map(Anime::date)
                .distinct()
                .collect(Collectors.toList())
                .stream()
                .map(day ->
                        new ReturnTitleGrids(
                                day,
                                animeDay.stream()
                                        .filter(e -> day.contains(e.date()))
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

    public static Boolean checkElapsed(Long unixtime, ZoneId zoneId) {
        Comparator<ZonedDateTime> comparator = Comparator.comparing(zdt -> zdt.truncatedTo(ChronoUnit.DAYS));
        Instant instant = Instant.ofEpochSecond(unixtime);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        ZonedDateTime today = ZonedDateTime.now(zoneId);
        return !(comparator.compare(zonedDateTime, today) >= 0);
    }

    public static List<Ratings> createRatings(Object[]... args) {
        return Arrays.stream(args)
                .filter(arg -> (Double) arg[1] > 0)
                .map(arg -> new Ratings()
                        .dbname((String) arg[0])
                        .score((Double) arg[1]))
                .collect(Collectors.toList());
    }

    public static List<Links> createLinks(Object[]... args) {
        return Arrays.stream(args)
                .filter(arg -> arg[2] != null)
                .map(arg -> new Links()
                        .name((String) arg[0])
                        .link((arg[1] != null) ? (String) arg[1] + arg[2] : (String) arg[2]))
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

    public static String createImagePath(Boolean downloaded, ImageType imageType, Long id) {
        String path = "/images/noimage.svg";
        if (downloaded) {
            if (imageType.equals(ImageType.full))
                path = "/images/anime/" + id.toString() + ".jpg";
            else if (imageType.equals(ImageType.thumbnail))
                path = "/images/anime/thumbnails/" + id.toString() + ".jpg";
        }
        return path;
    }

    public static String createDateStart(Integer firstYear, Integer firstMonth) {
        if (firstYear != null && firstMonth != null)
            return firstYear != 0 ? firstMonth >= 10 ? firstYear + "-" + firstMonth : firstYear + "-0" + firstMonth : null;
        else return null;
    }

    public static Boolean createRecommended(Double... ratings) {
        OptionalDouble average = Arrays.stream(ratings)
                .mapToDouble(a -> a)
                .average();
        return average.isPresent() && (average.getAsDouble() >= 7.3);
    }
}
