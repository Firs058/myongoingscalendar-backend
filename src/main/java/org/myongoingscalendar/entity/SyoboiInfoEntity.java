package org.myongoingscalendar.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author firs
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "syoboi_info")
public class SyoboiInfoEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "syoboi_info_seq", sequenceName = "syoboi_info_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "syoboi_info_seq")
    private Long id;
    @JoinColumn(name = "tid", referencedColumnName = "tid", unique = true, nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private OngoingEntity ongoingEntity;
    @JsonProperty("Title")
    @Column(columnDefinition = "text")
    private String title;
    @JsonProperty("ShortTitle")
    @Column(name = "short_title", columnDefinition = "text")
    private String shortTitle;
    @JsonProperty("TitleYomi")
    @Column(name = "title_yomi", columnDefinition = "text")
    private String titleYomi;
    @JsonProperty("TitleEN")
    @Column(name = "title_en", columnDefinition = "text")
    private String titleEN;
    @JsonProperty("Cat")
    @Column
    private Integer cat;
    @JsonProperty("FirstCh")
    @Column(name = "first_ch", columnDefinition = "text")
    private String firstCh;
    @JsonProperty("FirstYear")
    @Column(name = "first_year")
    private Integer firstYear;
    @JsonProperty("FirstMonth")
    @Column(name = "first_month")
    private Integer firstMonth;
    @JsonProperty("FirstEndYear")
    @Column(name = "first_end_year")
    private Integer firstEndYear;
    @JsonProperty("FirstEndMonth")
    @Column(name = "first_end_month")
    private Integer firstEndMonth;
    @JsonProperty("TitleFlag")
    @Column(name = "title_flag")
    private Integer titleFlag;
    @JsonProperty("Keywords")
    @Column(columnDefinition = "text")
    private String keywords;
    @JsonProperty("UserPoint")
    @Column(name = "user_point")
    private Integer userPoint;
    @JsonProperty("UserPointRank")
    @Column(name = "user_point_rank")
    private Integer userPointRank;
    @JsonProperty("TitleViewCount")
    @Column(name = "title_view_count")
    private Integer titleViewCount;
    @JsonProperty("Comment")
    @Column(columnDefinition = "text")
    private String comment;
    @JsonProperty("SubTitles")
    @Column(columnDefinition = "text")
    private String subtitles;
    @Transient
    private Boolean finished;

    @PostLoad
    private void onLoad() {
        try {
            this.finished = new SimpleDateFormat("MM-yyyy").parse(this.firstEndMonth + "-" + this.firstEndYear).before(new Date());
        } catch (ParseException e) {
            this.finished = false;
        }
    }
}
