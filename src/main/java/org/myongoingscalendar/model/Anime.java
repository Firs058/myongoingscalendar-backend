package org.myongoingscalendar.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Anime {
    @JsonIgnore
    private int id;
    @JsonIgnore
    private String ja;
    @JsonIgnore
    private String en;
    @JsonIgnore
    private String summary;
    @JsonIgnore
    private Date datestart;
    private BigInteger tid;
    private String time;
    @JsonIgnore
    private Day day;
    private String title;
    private String titleen;
    private int episode;
    private String shift;
    private String channel;
    @JsonIgnore
    private String subtitles;
    @JsonIgnore
    private BigInteger aid;
    @JsonIgnore
    private Boolean imagedownloaded;
    @JsonIgnore
    private String vibrant;
    private Image image;
}