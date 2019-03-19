package org.myongoingscalendar.elastic.service;

import org.myongoingscalendar.model.ElasticQuery;
import org.myongoingscalendar.model.SearchResult;
import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.myongoingscalendar.model.SortedOngoings;

import java.util.List;
import java.util.Optional;

public interface ElasticAnimeService {
    ElasticAnime save(ElasticAnime anime);

    void delete(ElasticAnime anime);

    Optional<ElasticAnime> findByTid(Long tid);

    List<ElasticAnime> findByTids(List<Long> tids);

    Iterable<ElasticAnime> findAll();

    SearchResult autocomplete(ElasticQuery elasticQuery, int size);

    List<SortedOngoings> getCurrentOngoingsList();
}