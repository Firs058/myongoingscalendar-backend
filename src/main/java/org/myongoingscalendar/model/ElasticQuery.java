package org.myongoingscalendar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class ElasticQuery {
    private String query;
    private Integer page;
    private Integer[] genres;
    private String[] scores;
    private String[] years;
}
