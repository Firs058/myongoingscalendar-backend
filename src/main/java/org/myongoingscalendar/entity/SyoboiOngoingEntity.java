package org.myongoingscalendar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
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
@Table(name = "syoboi_ongoings", indexes = {
        @Index(columnList = "date_start", name = "syoboi_ongoings_date_start_index")
})
public class SyoboiOngoingEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "syoboi_ongoings_seq", sequenceName = "syoboi_ongoings_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "syoboi_ongoings_seq")
    private Long id;
    @Column(name = "date_start", columnDefinition = "text", nullable = false)
    private String dateStart;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_revision", columnDefinition = "timestamp with time zone", nullable = false)
    private Date lastRevision;
    @JoinColumn(name = "tid", referencedColumnName = "tid", unique = true, nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private OngoingEntity ongoingEntity;
}
