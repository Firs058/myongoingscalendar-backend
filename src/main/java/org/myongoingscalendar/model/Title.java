package org.myongoingscalendar.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.myongoingscalendar.entity.GenreEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Title {
    private Long tid;
    private Image image;
    private String ja;
    private String en;
    private String description;
    private String trailer;
    private List<Ratings> ratings = new ArrayList<>();
    private ChartData chartData;
    private int episodes;
    private List<Links> links = new ArrayList<>();
    private List<GenreEntity> genres = new ArrayList<>();
    private Integer firstyear;
    private Integer firstmonth;
}
