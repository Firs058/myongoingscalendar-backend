package org.myongoingscalendar.elastic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.myongoingscalendar.model.Image;
import org.myongoingscalendar.model.Ratings;
import org.myongoingscalendar.entity.ChannelEntity;
import org.myongoingscalendar.entity.GenreEntity;
import org.myongoingscalendar.model.WatchingStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.List;

@Document(indexName = "animes", type = "anime")
@Setting(settingPath = "/settings/settings.json")
@Mapping(mappingPath = "/mappings/mappings.json")
@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
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
    private List<Ratings> ratings;
    private List<ChannelEntity> channels;
    private WatchingStatus watchingStatus;
    private Boolean outdated;
}