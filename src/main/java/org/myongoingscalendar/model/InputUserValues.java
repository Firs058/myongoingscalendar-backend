package org.myongoingscalendar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputUserValues {
    private Integer tid;
    private String timezone;
}
