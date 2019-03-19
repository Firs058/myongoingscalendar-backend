package org.myongoingscalendar.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.myongoingscalendar.entity.GenreEntity;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ElasticInfo {
    private List<GenreEntity> genres;
    private List<Integer> years;
    private int[] scores;
}
