package org.myongoingscalendar.model.Statistics;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.myongoingscalendar.elastic.model.ElasticAnime;
import org.myongoingscalendar.model.WatchingStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Statistics {
    private ScoresSection scoresSection;
    private GenresSection genresSection;
    private AnimeSection animeSection;
    private CommentsSection commentsSection;
}
