package org.myongoingscalendar.manipulations;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.myongoingscalendar.entity.*;
import org.myongoingscalendar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
public class ParseSyoboiManipulations {

    @Value("${parse.syoboi.rss}")
    private String syoboiRss;
    @Value("${parse.syoboi.path}")
    private String syoboiPath;
    private final OngoingService ongoingService;
    private final SyoboiOngoingService syoboiOngoingService;
    private final SyoboiInfoService syoboiInfoService;
    private final ChannelService channelService;

    @Autowired
    public ParseSyoboiManipulations(OngoingService ongoingService, SyoboiOngoingService syoboiOngoingService, SyoboiInfoService syoboiInfoService, ChannelService channelService) {
        this.ongoingService = ongoingService;
        this.syoboiOngoingService = syoboiOngoingService;
        this.syoboiInfoService = syoboiInfoService;
        this.channelService = channelService;
    }

    public void parseSyoboiRSS() {
        try {
            List<OngoingEntity> tempOngoingEntityList = new ArrayList<>();

            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(syoboiRss).openConnection();
            httpURLConnection.setConnectTimeout(60000);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed;
            feed = input.build(new XmlReader(httpURLConnection));
            List<SyndEntry> entries = feed.getEntries();

            for (SyndEntry entry : entries) {
                String[] tokens = entry.getTitle().split("[|]");
                DateTimeFormatter format = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
                LocalDateTime dateTime = format.parseLocalDateTime(new DateTime().getYear() + "/" + tokens[2]);
                if (DateTimeZone.getDefault().isLocalDateTimeGap(dateTime)) dateTime = dateTime.plusHours(1);
                Long tid = Long.parseLong(tokens[0]);
                Timestamp date = new Timestamp(dateTime.toDateTime().getMillis());
                String channel = tokens[6].trim();

                if (tempOngoingEntityList.stream().noneMatch(c -> c.tid().equals(tid)))
                    tempOngoingEntityList.add(
                            new OngoingEntity()
                                    .tid(tid)
                                    .syoboiRssEntities(new ArrayList<>()));

                tempOngoingEntityList.stream()
                        .filter(c -> c.tid().equals(tid))
                        .findFirst()
                        .ifPresent(m -> m.syoboiRssEntities().add(
                                new SyoboiRssEntity()
                                        .ongoingEntity(new OngoingEntity().tid(m.tid()))
                                        .date(date)
                                        .channel(channel)
                                )
                        );
            }

            List<Long> tids = tempOngoingEntityList.stream().map(OngoingEntity::tid).distinct().toList();

            List<OngoingEntity> existentList = ongoingService.findByTidIn(tids);

            existentList.forEach(e ->
                    tempOngoingEntityList.stream()
                            .filter(t -> t.tid().equals(e.tid()))
                            .findFirst()
                            .map(freshOngoingsEntity -> e.syoboiRssEntities(freshOngoingsEntity.syoboiRssEntities()))
                            .orElseGet(() -> e.syoboiRssEntities(null))
            );

            if (existentList.size() > 0) ongoingService.saveAll(existentList);

            List<OngoingEntity> notInDB = tempOngoingEntityList.stream()
                    .filter(os -> existentList.stream().noneMatch(ns -> os.tid().equals(ns.tid())))
                    .toList();

            if (notInDB.size() > 0) ongoingService.saveAll(notInDB);
        } catch (FeedException | IOException e) {
            log.error("Can't parse syoboi RSS", e);
        }
    }

    public void parseSyoboiAnimeOngoingsList() {
        try {
            List<OngoingEntity> tempOngoingEntityList = new ArrayList<>();

            org.jsoup.nodes.Document doc = Jsoup.connect("http://cal.syoboi.jp/list?cat=1").userAgent("Mozilla").get();
            Elements table = doc.select("table.TitleList");
            Elements rows = table.select("tr");
            IntStream.range(1, rows.size())
                    .forEach(i -> {
                        Element titleNameElement = rows.get(i).select("td").first();
                        Element dateStartElement = titleNameElement.nextElementSibling();
                        Element idElement = dateStartElement.nextElementSibling().nextElementSibling();
                        Element lastRevisionElement = idElement.nextElementSibling();

                        Long tid = Long.parseLong(idElement.text().trim());
                        String dateStart = dateStartElement.text().trim();
                        Date lastRevision = Timestamp.valueOf(lastRevisionElement.text().trim());

                        OngoingEntity ongoing = new OngoingEntity().tid(tid);

                        tempOngoingEntityList.add(
                                ongoing
                                        .syoboiOngoingEntity(
                                                new SyoboiOngoingEntity()
                                                        .ongoingEntity(ongoing)
                                                        .dateStart(dateStart)
                                                        .lastRevision(lastRevision))
                        );
                    });

            List<Long> tids = tempOngoingEntityList.stream().map(OngoingEntity::tid).distinct().toList();

            List<OngoingEntity> existentList = ongoingService.findByTidIn(tids);

            existentList.forEach(e ->
                    tempOngoingEntityList.stream()
                            .filter(t -> t.tid().equals(e.tid()))
                            .findFirst()
                            .map(f -> e.syoboiOngoingEntity(f.syoboiOngoingEntity()))
            );

            if (existentList.size() > 0) ongoingService.saveAll(existentList);

            List<OngoingEntity> notInDB = tempOngoingEntityList.stream()
                    .filter(os -> existentList.stream().noneMatch(ns -> os.tid().equals(ns.tid())))
                    .toList();

            if (notInDB.size() > 0) ongoingService.saveAll(notInDB);

            List<OngoingEntity> toDelete = ongoingService.getCurrentOngoings().stream()
                    .filter(os -> tempOngoingEntityList.stream().noneMatch(ns -> os.tid().equals(ns.tid())))
                    .toList();

            toDelete.forEach(e -> e.syoboiOngoingEntity(null));

            if (toDelete.size() > 0) ongoingService.saveAll(toDelete);
        } catch (IOException e) {
            log.error("Can't parse syoboi ongoings", e);
        }
    }

    @Transactional
    public void parseSyoboiUidTimetableForAllOngoings() {
        parseList(syoboiOngoingService.getAll().stream().map(e -> e.ongoingEntity().tid()).distinct().toList());
    }

    @Transactional
    void parseSyoboiUidTimetable(Long tid) {
        ongoingService.findByTid(tid).ifPresent(ongoing -> {
            List<SyoboiTimetableEntity> syoboiTimetableEntityList = new ArrayList<>();
            try {
                org.jsoup.nodes.Document doc = Jsoup.connect("http://cal.syoboi.jp/tid/".concat(tid.toString()).concat("/time?Filter=ChAll&Filter2=Recent")).userAgent("Mozilla").get();
                Elements table = doc.select("table#ProgList.progs");
                Elements rows = table.select("tr");
                IntStream.range(1, rows.size())
                        .forEach(i -> {
                            Element ch = rows.get(i).select("td").first();
                            String shift = rows.get(i).select("span.peOffset").text();
                            Element dateStart = rows.get(i).select("td.start").first();
                            if (!shift.equals("")) dateStart.select("span.peOffset").remove();
                            Element episode = rows.get(i).select("td.count").first();
                            Element episodeName = rows.get(i).select("td.subtitle").first();
                            episodeName.select("span.connect").remove();
                            episodeName.select("div.peComment").remove();
                            if (episodeName.childNodeSize() > 1) episodeName.select("div.peNotice").remove();
                            if (!episode.text().equals("")) {
                                String channelName = ch.text().trim();
                                Optional<ChannelEntity> channel = channelService.findByJa(channelName)
                                        .or(() -> channelService.save(new ChannelEntity().ja(channelName)));
                                channel.ifPresent(channelEntity ->
                                        syoboiTimetableEntityList.add(
                                                new SyoboiTimetableEntity()
                                                        .ongoingEntity(ongoing)
                                                        .channelEntity(channelEntity)
                                                        .dateStart(parseJapanIdioticTimeSystem(dateStart.text()))
                                                        .shift(parseEpisodeShift(shift))
                                                        .episode(Integer.parseInt(episode.text()))
                                                        .episodeName(parseTidEpisodeName(episodeName.text()))
                                        ));
                            }
                        });
                ongoing.syoboiTimetableEntities().clear();
                if (!syoboiTimetableEntityList.isEmpty())
                    ongoing.syoboiTimetableEntities().addAll(syoboiTimetableEntityList);
                ongoing.syoboiRssEntities().forEach(s -> s.updated(true));
                ongoingService.save(ongoing);
            } catch (IOException e) {
                log.error("Cant get syoboi tid timetable: " + tid + ", " + e.toString());
            }
        });
    }

    @Transactional
    public void updateTidsTimetable() {
        List<Long> toParse = syoboiOngoingService.getAll().stream()
                .flatMap(e -> e.ongoingEntity().syoboiRssEntities().stream())
                .filter(a -> a.updated().equals(false))
                .map(m -> m.ongoingEntity().tid())
                .distinct()
                .toList();
        parseList(toParse);
    }

    private void parseList(List<Long> tids) {
        for (Long tid : tids) {
            try {
                parseSyoboiUidTimetable(tid);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }
    }

    public void insertFromSyoboiToInfoForAll() {
        parse(ongoingService.getAll().stream().map(OngoingEntity::tid).distinct().toList());
    }

    public void insertFromSyoboiToInfoForEmptyInfo() {
        parse(ongoingService.getCurrentOngoingsWithoutInfo().stream().map(OngoingEntity::tid).distinct().toList());
    }

    public void insertFromSyoboiToInfoForOldOngoingsAndCurrentOngoings() {
        List<Long> oldOngoings = syoboiInfoService.findAllByFirstEndMonthIsNullAndFirstEndYearIsNull().stream()
                .map(e -> e.ongoingEntity().tid())
                .distinct()
                .toList();
        List<Long> currentOngoings = syoboiOngoingService.getAll().stream()
                .map(e -> e.ongoingEntity().tid())
                .distinct()
                .toList();
        parse(Stream.concat(oldOngoings.stream(), currentOngoings.stream()).distinct().toList());
    }

    private void parse(List<Long> tids) {
        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Long tid : tids) {
            try {
                HttpURLConnection httpConn = (HttpURLConnection) new URL(syoboiPath + tid).openConnection();
                httpConn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                httpConn.setConnectTimeout(60000);
                InputStream inputStream = new BufferedInputStream(httpConn.getInputStream());

                JsonParser jp = jsonFactory.createParser(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                JsonNode root = objectMapper.readTree(jp);
                JsonNode nodeTID = root.findValue(String.valueOf(tid));

                ongoingService.findByTid(tid).ifPresent(s -> {
                    s.syoboiInfoEntity(new ObjectMapper().convertValue(nodeTID, SyoboiInfoEntity.class).ongoingEntity(s));
                    ongoingService.save(s);
                });
                Thread.sleep(3000);
            } catch (IOException | InterruptedException e) {
                log.error("Error parse syoboi info " + tid, e);
            }
        }
    }

    private Date parseJapanIdioticTimeSystem(String input) {
        Pattern pattern = Pattern.compile("^(\\d+-\\d+-\\d+)\\D+(\\d+):(\\d+)");
        Matcher m = pattern.matcher(input);
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZone(DateTimeZone.forID("Asia/Tokyo"));
        if (m.matches()) {
            if (Integer.parseInt(m.group(2)) < 24)
                return format.parseDateTime(m.group(1) + " " + m.group(2) + ":" + m.group(3)).toDate();

            int trueHour = Integer.parseInt(m.group(2)) - 24;
            DateTime datetime = format.parseDateTime(m.group(1) + " " + trueHour + ":" + m.group(3));
            MutableDateTime mu = new MutableDateTime(datetime);
            mu.addDays(1);
            return mu.toDate();
        }
        return null;
    }

    private String parseTidEpisodeName(String episodeName) {
        Pattern pattern = Pattern.compile("^(\\S+)\\s+(\\W+\\S+\\W+)");
        Matcher m = pattern.matcher(episodeName);
        return m.matches() ? m.group(1) : episodeName;
    }

    private String parseEpisodeShift(String shift) {
        Pattern pattern = Pattern.compile("^(\\W+\\d+)");
        Matcher m = pattern.matcher(shift);
        return m.matches() ? m.group(1) : "0";
    }
}
