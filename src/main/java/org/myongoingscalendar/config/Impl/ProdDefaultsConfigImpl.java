package org.myongoingscalendar.config.Impl;

import org.myongoingscalendar.config.DefaultsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Profile({"prod"})
public class ProdDefaultsConfigImpl implements DefaultsConfig {
    @Value("${hosts.prod.domain}")
    private String domain;
    @Value("${hosts.prod.scheme}")
    private String scheme;

    @Override
    public UriComponentsBuilder getURLBuilder() {
        return UriComponentsBuilder.newInstance().scheme(scheme).host(domain);
    }
}
