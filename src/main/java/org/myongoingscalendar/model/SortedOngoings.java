package org.myongoingscalendar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.myongoingscalendar.elastic.model.ElasticAnime;

import java.util.List;

/**
 * @author firs
 */
@Data
@AllArgsConstructor
public class SortedOngoings {
    private String dateStart;
    private List<ElasticAnime> animes;
}
