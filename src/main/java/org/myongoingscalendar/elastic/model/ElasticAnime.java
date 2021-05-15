package org.myongoingscalendar.elastic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.myongoingscalendar.model.Image;
import org.myongoingscalendar.model.Rating;
import org.myongoingscalendar.entity.ChannelEntity;
import org.myongoingscalendar.entity.GenreEntity;
import org.myongoingscalendar.model.WatchingStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Document(indexName = "animes")
@Setting(settingPath = "/settings/settings.json")
@Mapping(mappingPath = "/mappings/mappings.json")
@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticAnime {
    @Id
    private Long tid;
    private String ja;
    private String en;
    private Image image;
    private String description;
    private String dateStart;
    private Integer episodes;
    private Boolean recommended;
    private List<GenreEntity> genres;
    private List<Rating> ratings;
    private List<ChannelEntity> channels;
    private WatchingStatus watchingStatus;
    private Boolean finished;
    private Boolean started;
    private Boolean favorite;
    private BigDecimal score;
}