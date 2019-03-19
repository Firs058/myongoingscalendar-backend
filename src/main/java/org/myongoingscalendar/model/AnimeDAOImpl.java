package org.myongoingscalendar.model;

import lombok.extern.slf4j.Slf4j;
import org.myongoingscalendar.service.CommentServiceCustom;
import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.myongoingscalendar.elastic.service.ElasticAnimeService;
import org.myongoingscalendar.manipulations.DBManipulations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnimeDAOImpl implements AnimeDAO {
    private final JdbcTemplate jdbcTemplate;
    private final DBManipulations dbManipulations;
    private final ElasticAnimeService elasticAnimeService;
    private final CommentServiceCustom commentServiceCustom;

    @Autowired
    public AnimeDAOImpl(JdbcTemplate jdbcTemplate, DBManipulations dbManipulations, ElasticAnimeService elasticAnimeService, CommentServiceCustom commentServiceCustom) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbManipulations = dbManipulations;
        this.elasticAnimeService = elasticAnimeService;
        this.commentServiceCustom = commentServiceCustom;
    }

    @Override
    @Cacheable("getAllOngoingsList")
    public List<TitlesList> getAllOngoingsList() {
        String sql = "SELECT\n" +
                "  o.title     AS ja,\n" +
                "  o.tid       AS tid\n" +
                "FROM ongoings o";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, row) ->
                new TitlesList()
                        .tid(rs.getInt("tid"))
                        .ja(rs.getString("ja"))
        ));
    }
}
