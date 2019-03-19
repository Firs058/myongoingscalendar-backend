package org.myongoingscalendar.elastic.service;

import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilders;
import org.myongoingscalendar.entity.OngoingEntity;
import org.myongoingscalendar.model.ElasticQuery;
import org.myongoingscalendar.model.SearchResult;
import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.myongoingscalendar.elastic.repository.AnimeRepository;
import org.myongoingscalendar.model.SortedOngoings;
import org.myongoingscalendar.service.OngoingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service
public class ElasticAnimeServiceImpl implements ElasticAnimeService {

    private final AnimeRepository animeRepository;
    private final OngoingService ongoingService;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public ElasticAnimeServiceImpl(AnimeRepository animeRepository, OngoingService ongoingService, ElasticsearchTemplate elasticsearchTemplate) {
        this.animeRepository = animeRepository;
        this.ongoingService = ongoingService;
        this.elasticsearchTemplate = elasticsearchTemplate;
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
        return (List<ElasticAnime>) animeRepository.findAllById(tids);
    }

    public Iterable<ElasticAnime> findAll() {
        return animeRepository.findAll();
    }

    public SearchResult autocomplete(ElasticQuery elasticQuery, int size) {
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

        SearchQuery withQuery = new NativeSearchQueryBuilder()
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

        SearchQuery withoutQuery = new NativeSearchQueryBuilder()
                .withFilter(filters)
                .withSort(SortBuilders.fieldSort("en.raw"))
                .withPageable(PageRequest.of((elasticQuery.page() >= 1 ? elasticQuery.page() : 1) - 1, size))
                .build();

        return elasticsearchTemplate.query(elasticQuery.query().length() != 0 ? withQuery : withoutQuery, response -> {
            long totalHits = response.getHits().getTotalHits();
            List<Map<String, Object>> animes = new ArrayList<>();
            response.getHits().forEach(hit -> animes.add(hit.getSourceAsMap()));
            return new SearchResult(animes, totalHits);
        });
    }

    @Override
    @Cacheable("getCurrentOngoingsList")
    public List<SortedOngoings> getCurrentOngoingsList() {
        List<ElasticAnime> elasticAnimes = findByTids(ongoingService.getCurrentOngoings().stream().map(OngoingEntity::tid).collect(Collectors.toList())).stream()
                .filter(Objects::nonNull)
                .filter(e -> Objects.nonNull(e.dateStart()))
                .collect(Collectors.toList());
        return elasticAnimes.stream()
                .map(ElasticAnime::dateStart)
                .distinct()
                .map(start ->
                        new SortedOngoings(
                                start,
                                elasticAnimes.stream()
                                        .filter(e -> start.contains(e.dateStart()))
                                        .collect(Collectors.toList())
                        ))
                .sorted(Comparator.comparing(SortedOngoings::getDateStart).reversed())
                .collect(Collectors.toList());
    }
}