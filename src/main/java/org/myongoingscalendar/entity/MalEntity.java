package org.myongoingscalendar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
@Table(name = "mal")
public class MalEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "mal_seq", sequenceName = "mal_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mal_seq")
    private Long id;
    @JoinColumn(name = "tid", referencedColumnName = "tid", unique = true, nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private OngoingEntity ongoingEntity;
    @Column(columnDefinition = "text", nullable = false)
    private String description;
    @Column(name = "trailer_url", columnDefinition = "text")
    private String trailerUrl;
}
