package org.myongoingscalendar.elastic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilders;
import org.myongoingscalendar.model.ElasticQuery;
import org.myongoingscalendar.model.SearchResult;
import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.myongoingscalendar.elastic.repository.AnimeRepository;
import org.myongoingscalendar.model.SortedOngoings;
import org.myongoingscalendar.service.OngoingService;
import org.myongoingscalendar.service.UserTitleDropService;
import org.myongoingscalendar.service.UserTitleService;
import org.myongoingscalendar.utils.AnimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class ElasticAnimeServiceImpl implements ElasticAnimeService {

    private final AnimeRepository animeRepository;
    private final OngoingService ongoingService;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final UserTitleDropService userTitleDropService;
    private final UserTitleService userTitleService;

    @Autowired
    public ElasticAnimeServiceImpl(AnimeRepository animeRepository, OngoingService ongoingService, ElasticsearchRestTemplate elasticsearchRestTemplate, UserTitleDropService userTitleDropService, UserTitleService userTitleService) {
        this.animeRepository = animeRepository;
        this.ongoingService = ongoingService;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
        this.userTitleDropService = userTitleDropService;
        this.userTitleService = userTitleService;
    }

    public ElasticAnime save(ElasticAnime anime) {
        return animeRepository.save(anime);
    }

    public void delete(ElasticAnime anime) {
        animeRepository.delete(anime);
    }

    public Optional<ElasticAnime> findByTid(Long tid) {
        return animeRepository.findById(tid);
    }

    public List<ElasticAnime> findByTids(List<Long> tids) {
        List<ElasticAnime> elasticAnimeList = (List<ElasticAnime>) animeRepository.findAllById(tids);
        return elasticAnimeList.stream()
                .filter(Objects::nonNull)
                .filter(e -> Objects.nonNull(e.dateStart()))
                .collect(Collectors.toList());
    }

    public Iterable<ElasticAnime> findAll() {
        return animeRepository.findAll();
    }

    private SearchResult makeQuery(ElasticQuery elasticQuery, int size) {
        BoolQueryBuilder filters = new BoolQueryBuilder();
        if (elasticQuery.page() == null) elasticQuery.page(1);

        if (elasticQuery.genres() != null && elasticQuery.genres().length != 0)
            filters.filter(termsQuery("genres.id", (Object[]) elasticQuery.genres()));
        if (elasticQuery.scores() != null && elasticQuery.scores().length != 0)
            filters.filter(rangeQuery("ratings.score")
                    .gte(elasticQuery.scores()[0])
                    .lte(elasticQuery.scores()[1]));
        if (elasticQuery.years() != null && elasticQuery.years().length != 0)
            filters.filter(rangeQuery("dateStart").format("YYYY")
                    .gte(elasticQuery.years()[0])
                    .lte(elasticQuery.years()[1]));

        NativeSearchQuery withQuery = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.boolQuery()
                                .must(multiMatchQuery(elasticQuery.query() != null ? elasticQuery.query() : "")
                                        .field("en")
                                        .field("ja")
                                        .type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX))
                                .filter(filters))
                .withSort(SortBuilders.fieldSort("en.raw"))
                .withPageable(PageRequest.of((elasticQuery.page() >= 1 ? elasticQuery.page() : 1) - 1, size))
                .build();

        NativeSearchQuery withoutQuery = new NativeSearchQueryBuilder()
                .withFilter(filters)
                .withSort(SortBuilders.fieldSort("en.raw"))
                .withPageable(PageRequest.of((elasticQuery.page() >= 1 ? elasticQuery.page() : 1) - 1, size))
                .build();

        SearchHits<ElasticAnime> elasticAnimePage = elasticsearchRestTemplate.search(elasticQuery.query().length() != 0 ? withQuery : withoutQuery, ElasticAnime.class, IndexCoordinates.of("animes"));
        List<ElasticAnime> animes = elasticAnimePage.get().map(SearchHit::getContent).collect(Collectors.toList());
        return new SearchResult(animes, elasticAnimePage.getTotalHits());
    }

    public SearchResult autocomplete(ElasticQuery elasticQuery, int size) {
        return makeQuery(elasticQuery, size);
    }

    @Transactional
    public SearchResult autocompleteForUser(ElasticQuery elasticQuery, int size, Long userid) {
        SearchResult searchResult = makeQuery(elasticQuery, size);
        List<Long> searchedTids = searchResult.getAnimes().stream().map(ElasticAnime::tid).collect(Collectors.toList());

        List<Long> addedTids = userTitleService.getCurrentOngoingsTidsAddedByUser(searchedTids, userid);
        List<Long> droppedTids = userTitleDropService.getCurrentOngoingsTidsDroppedByUser(searchedTids, userid);

        searchResult.setAnimes(AnimeUtil.createWatchingStatus(searchResult.getAnimes(), addedTids, droppedTids));
        return searchResult;
    }

    @Override
    @Cacheable("getCurrentOngoingsList")
    public List<SortedOngoings> getCurrentOngoingsList() {
        List<ElasticAnime> elasticAnimes = findByTids(ongoingService.getCurrentOngoingsTids());
        List<ElasticAnime> elasticAnimesWithWatchingStatus = AnimeUtil.createWatchingStatus(elasticAnimes, Collections.emptyList(), Collections.emptyList());
        return sortCurrentOngoingsList(elasticAnimesWithWatchingStatus);
    }

    @Transactional
    @Override
    public List<SortedOngoings> getUserCurrentOngoingsList(Long userid) {
        List<Long> currentOngoingsTids = ongoingService.getCurrentOngoingsTids();
        List<ElasticAnime> elasticAnimes = findByTids(currentOngoingsTids);

        List<Long> addedTids = userTitleService.getCurrentOngoingsTidsAddedByUser(currentOngoingsTids, userid);
        List<Long> droppedTids = userTitleDropService.getCurrentOngoingsTidsDroppedByUser(currentOngoingsTids, userid);

        List<ElasticAnime> elasticAnimesWithWatchingStatus = AnimeUtil.createWatchingStatus(elasticAnimes, addedTids, droppedTids);
        return sortCurrentOngoingsList(elasticAnimesWithWatchingStatus);
    }

    private List<SortedOngoings> sortCurrentOngoingsList(List<ElasticAnime> elasticAnimes) {
        return elasticAnimes.stream()
                .map(ElasticAnime::dateStart)
                .distinct()
                .map(start ->
                        new SortedOngoings(
                                start,
                                elasticAnimes.stream()
                                        .filter(e -> start.contains(e.dateStart()))
                                        .sorted(Comparator.comparing(ElasticAnime::en, Comparator.nullsLast(Comparator.naturalOrder())))
                                        .collect(Collectors.toList())
                        ))
                .sorted(Comparator.comparing(SortedOngoings::getDateStart).reversed())
                .collect(Collectors.toList());
    }
}