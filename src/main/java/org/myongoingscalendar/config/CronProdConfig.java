package org.myongoingscalendar.config;

import org.myongoingscalendar.elastic.FillElastic;
import org.myongoingscalendar.manipulations.ParseAniDBManipulations;
import org.myongoingscalendar.manipulations.ParseAnnManipulations;
import org.myongoingscalendar.manipulations.ParseMALManipulations;
import org.myongoingscalendar.manipulations.ParseSyoboiManipulations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Profile("prod")
public class CronProdConfig {

    private final ParseSyoboiManipulations parseSyoboiManipulations;
    private final ParseAniDBManipulations parseAniDBManipulations;
    private final ParseMALManipulations parseMALManipulations;
    private final ParseAnnManipulations parseAnnManipulations;
    private final FillElastic fillElastic;

    @Autowired
    public CronProdConfig(ParseSyoboiManipulations parseSyoboiManipulations, ParseAniDBManipulations parseAniDBManipulations, ParseMALManipulations parseMALManipulations, ParseAnnManipulations parseAnnManipulations, FillElastic fillElastic) {
        this.parseSyoboiManipulations = parseSyoboiManipulations;
        this.parseAniDBManipulations = parseAniDBManipulations;
        this.parseMALManipulations = parseMALManipulations;
        this.parseAnnManipulations = parseAnnManipulations;
        this.fillElastic = fillElastic;
    }

    @Scheduled(cron = "* */10 * * * ?")
    public void updateSyoboi() {
        parseSyoboiManipulations.parseSyoboiRSS();
        parseSyoboiManipulations.updateTidsTimetable();
        parseSyoboiManipulations.insertFromSyoboiToInfoForEmptyInfo();
        fillElastic.loadAnimeIntoElastic();
        clearOngoingsListCache();
    }

    @Scheduled(cron = "0 * */6 * * ?")
    public void updateSyoboiOngoingsList() {
        parseSyoboiManipulations.parseSyoboiAnimeOngoingsList();
        parseSyoboiManipulations.parseSyoboiUidTimetableForAllOngoings();
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void updateDBData() {
        parseAniDBManipulations.parseAniDBForCurrentOngoings();
        parseAniDBManipulations.getAniDBImages();
        parseMALManipulations.parseMALForCurrentOngoings();
        parseAnnManipulations.parseAnnForCurrentOngoings();
        parseSyoboiManipulations.insertFromSyoboiToInfoForOldOngoingsAndCurrentOngoings();
        fillElastic.loadAnimeIntoElastic();
        clearOngoingsListCache();
    }

    @Scheduled(cron = "0 * * * * ?")
    @CacheEvict(value = {"getCurrentOngoingsList", "getAllOngoingsList"}, allEntries = true)
    public void clearOngoingsListCache() {
    }
}