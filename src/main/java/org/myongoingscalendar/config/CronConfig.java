package org.myongoingscalendar.config;

import org.myongoingscalendar.manipulations.ParseAniDBManipulations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

@Configuration
public class CronConfig {

    private final ParseAniDBManipulations parseAniDBManipulations;

    @Autowired
    public CronConfig(ParseAniDBManipulations parseAniDBManipulations) {
        this.parseAniDBManipulations = parseAniDBManipulations;
    }

    @Scheduled(cron = "* */10 * * * ?")
    @CacheEvict(value = {"getOngoingsFull", "getOngoingsMin", "getAllTimezones", "getAllGenres", "getYearsRanges"}, allEntries = true)
    public void clearCache() {
    }

    @PostConstruct
    @CacheEvict(value = "getImagesLocationPath", allEntries = true)
    public void getImagesLocationPath() {
        parseAniDBManipulations.getImagesLocationPath();
        parseAniDBManipulations.checkThumbnails();
    }
}