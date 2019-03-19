
package org.myongoingscalendar.model.Jikan;

import java.io.Serializable;

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
@JsonIgnoreProperties(ignoreUnknown=true)
public class Genre implements Serializable
{
    @JsonProperty("url")
    private String url;
    @JsonProperty("name")
    private String name;
}
