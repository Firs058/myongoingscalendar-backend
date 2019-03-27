package org.myongoingscalendar.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author firs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Datasets {
    private Object[] data;
    private String label;
    private String borderColor;
    private String backgroundColor;
    private String pointBackgroundColor = "rgba(255,255,255,0.25)";
    private String pointStyle = "line";
    private boolean fill = true;
}
