package org.myongoingscalendar.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReturnTitleGrids {
    private String day;
    private List<Anime> anime;
}
