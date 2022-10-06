
package org.myongoingscalendar.model.Jikan;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JikanAnime implements Serializable {
    @JsonProperty("title")
    private String title;
    @JsonProperty("title_english")
    private String titleEnglish;
    @JsonProperty("title_japanese")
    private String titleJapanese;
    @JsonProperty("title_synonyms")
    private Object titleSynonyms;
    @JsonProperty("score")
    private BigDecimal score;
    @JsonProperty("synopsis")
    private String synopsis;
    @JsonProperty("trailer")
    private Trailer trailer;
    @JsonProperty("genres")
    private List<Genre> genres = null;
}
