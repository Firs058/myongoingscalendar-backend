package org.myongoingscalendar.elastic.repository;

import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AnimeRepository extends ElasticsearchRepository<ElasticAnime, Long> {
}