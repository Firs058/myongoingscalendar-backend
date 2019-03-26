package org.myongoingscalendar.config;

import org.myongoingscalendar.elastic.FillElastic;
import org.myongoingscalendar.manipulations.ParseAniDBManipulations;
import org.myongoingscalendar.manipulations.ParseMALManipulations;
import org.myongoingscalendar.manipulations.ParseSyoboiManipulations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

@Configuration
public class CronConfig {

    private final ParseSyoboiManipulations parseSyoboiManipulations;
    private final ParseAniDBManipulations parseAniDBManipulations;
    private final ParseMALManipulations parseMALManipulations;
    private final FillElastic fillElastic;

    @Autowired
    public CronConfig(ParseSyoboiManipulations parseSyoboiManipulations, ParseAniDBManipulations parseAniDBManipulations, ParseMALManipulations parseMALManipulations, FillElastic fillElastic) {
        this.parseSyoboiManipulations = parseSyoboiManipulations;
        this.parseAniDBManipulations = parseAniDBManipulations;
        this.parseMALManipulations = parseMALManipulations;
        this.fillElastic = fillElastic;
    }

    @Profile({"prod"})
    @Scheduled(cron = "* */10 * * * ?")
    public void updateSyoboi() {
        parseSyoboiManipulations.parseSyoboiRSS();
        parseSyoboiManipulations.updateTidsTimetable();
        fillElastic.loadAnimeIntoElastic();
        clearOngoingsListCache();
    }

    @Profile({"prod"})
    @Scheduled(cron = "0 * */6 * * ?")
    public void updateSyoboiOngoingsList() {
        parseSyoboiManipulations.parseSyoboiAnimeOngoingsList();
        parseSyoboiManipulations.parseSyoboiUidTimetableForAllOngoings();
    }

    @Profile({"prod"})
    @Scheduled(cron = "0 0 12 * * ?")
    public void updateDBData() {
        parseAniDBManipulations.parseAniDB();
        parseAniDBManipulations.getAniDBImages();
        parseMALManipulations.parseMAL();
        parseSyoboiManipulations.insertFromSyoboiToInfo();
        clearOngoingsListCache();
    }

    @Profile({"prod", "dev"})
    @Scheduled(cron = "* */10 * * * ?")
    @CacheEvict(value = {"getOngoings", "getOngoingsMin", "getAllTimezones", "getAllGenres", "getFrontLocale", "getYearsRanges"}, allEntries = true)
    public void clearCache() {
    }

    @PostConstruct
    @CacheEvict(value = "getImagesLocationPath", allEntries = true)
    public void getImagesLocationPath() {
        parseAniDBManipulations.getImagesLocationPath();
        parseAniDBManipulations.checkThumbnails();
    }

    @Profile({"prod", "dev"})
    @Scheduled(cron = "0 * * * * ?")
    @CacheEvict(value = {"getCurrentOngoingsList", "getAllOngoingsList"}, allEntries = true)
    public void clearOngoingsListCache() {
    }
}