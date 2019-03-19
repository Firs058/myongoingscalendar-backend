package org.myongoingscalendar.config.Impl;

import org.myongoingscalendar.config.DefaultsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Profile({"dev"})
public class DevDefaultsConfigImpl implements DefaultsConfig {
    @Value("${hosts.dev.domain}")
    private String domain;
    @Value("${hosts.dev.scheme}")
    private String scheme;

    @Override
    public UriComponentsBuilder getURLBuilder() {
        return UriComponentsBuilder.newInstance().scheme(scheme).host(domain);
    }
}
