package org.myongoingscalendar.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class SearchResult {
    private List<Map<String, Object>> animes;
    private long count;
}
