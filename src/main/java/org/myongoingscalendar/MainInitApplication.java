package org.myongoingscalendar;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class MainInitApplication {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", SystemUtils.IS_OS_WINDOWS ? "dev" : "prod");
        SpringApplication.run(MainInitApplication.class, args);
    }
}
