package org.myongoingscalendar.manipulations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Service
public class DBManipulations {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBManipulations(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    @Cacheable("getAllTimezones")
    public List<String> getAllTimezones() {
        String sql = "SELECT p.name FROM pg_timezone_names p WHERE p.name NOT LIKE '%posix%'";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, row) -> rs.getString("name")));
    }

    @PostConstruct
    @Cacheable("getAllStopWords")
    public List<String> getAllStopWords() {
        String sql = "SELECT word FROM banned_words";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, row) -> rs.getString("word")));
    }
}
