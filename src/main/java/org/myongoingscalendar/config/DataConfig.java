package org.myongoingscalendar.config;

import org.myongoingscalendar.model.UrlDataDAO;
import org.myongoingscalendar.model.UrlDataImpl;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataConfig {

    @Bean
    public UrlDataDAO getUrlDataDAO() {
        return new UrlDataImpl();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "getOngoingsFull",
                "getOngoingsMin",
                "getAllTimezones",
                "getCurrentOngoingsList",
                "getImagesLocationPath",
                "getAllOngoingsList",
                "getAllGenres",
                "getAllStopWords",
                "getYearsRanges"
        );
    }
}