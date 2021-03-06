package org.myongoingscalendar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author firs
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Entity
@Table(name = "ratings")
public class RatingEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "ratings_seq", sequenceName = "ratings_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ratings_seq")
    private Long id;
    @JoinColumn(name = "tid", referencedColumnName = "tid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private OngoingEntity ongoingEntity;
    @Column(name = "anidb_temporary")
    private BigDecimal anidbTemporary;
    @Column(name = "anidb_permanent")
    private BigDecimal anidbPermanent;
    @Column(name = "mal")
    private BigDecimal mal;
    @Column(name = "ann")
    private BigDecimal ann;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp with time zone", nullable = false)
    private Date added = new Date();
}
