package org.myongoingscalendar.model.Statistics;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.myongoingscalendar.model.BaseComment;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class StatisticsComments {
    private ElasticAnime anime;
    private Integer count;
    private List<BaseComment> comments;
}
