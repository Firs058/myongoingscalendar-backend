package org.myongoingscalendar.manipulations;

import org.myongoingscalendar.config.DefaultsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class URLManipulations {

    private final DefaultsConfig defaultsConfig;

    @Autowired
    public URLManipulations(DefaultsConfig defaultsConfig) {
        this.defaultsConfig = defaultsConfig;
    }

    public String makeURL(String path) {
        return defaultsConfig.getURLBuilder().path(path).toUriString();
    }
}
