package org.myongoingscalendar.elastic;

import org.myongoingscalendar.elastic.service.ElasticAnimeService;
import org.myongoingscalendar.service.OngoingServiceCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FillElastic {
    private final ElasticAnimeService elasticAnimeService;
    private final OngoingServiceCustom ongoingServiceCustom;

    @Autowired
    public FillElastic(ElasticAnimeService elasticAnimeService, OngoingServiceCustom ongoingServiceCustom) {
        this.elasticAnimeService = elasticAnimeService;
        this.ongoingServiceCustom = ongoingServiceCustom;
    }

    public void loadAnimeIntoElastic() {
        ongoingServiceCustom.getElasticData().forEach(elasticAnimeService::save);
    }
}
