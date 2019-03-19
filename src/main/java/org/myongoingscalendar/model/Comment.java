package org.myongoingscalendar.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Comment {
    private Long id;
    private String path;
    private Long tid;
    private long added;
    private String text;
    private Integer replies;
    private UserMin user;
    private boolean liked;
    private boolean disliked;
    private int likes;
    private int dislikes;
}