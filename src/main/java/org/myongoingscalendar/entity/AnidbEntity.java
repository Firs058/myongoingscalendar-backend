package org.myongoingscalendar.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author firs
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Entity
@Table(name = "anidb")
@TypeDef(
        name = "jsonb-node", typeClass = JsonNodeBinaryType.class
)
public class AnidbEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "anidb_seq", sequenceName = "anidb_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anidb_seq")
    private Long id;
    @JoinColumn(name = "tid", referencedColumnName = "tid", unique = true, nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private OngoingEntity ongoingEntity;
    @Column(columnDefinition = "text", nullable = false)
    private String url;
    @Column(columnDefinition = "text", nullable = false)
    private String description = "Not have description";
    @Column(name = "episode_count", nullable = false)
    private Integer episodeCount = 0;
    @Column(columnDefinition = "text", nullable = false)
    private String picture;
    @Column(nullable = false)
    private Boolean image = false;
    @Column(name = "title_en", columnDefinition = "text")
    private String titleEN;
    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb")
    private JsonNode vibrant;
}
