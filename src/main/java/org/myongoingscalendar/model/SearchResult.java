package org.myongoingscalendar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.myongoingscalendar.elastic.model.ElasticAnime;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResult {
    private List<ElasticAnime> animes;
    private long count;
}
