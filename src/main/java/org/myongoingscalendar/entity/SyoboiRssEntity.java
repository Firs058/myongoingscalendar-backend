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
@Table(name = "syoboi_rss")
public class SyoboiRssEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "syoboi_rss_seq", sequenceName = "syoboi_rss_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "syoboi_rss_seq")
    private Long id;
    @JoinColumn(name = "tid", referencedColumnName = "tid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private OngoingEntity ongoingEntity;
    @Column(columnDefinition = "text", nullable = false)
    private String channel;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp with time zone", nullable = false)
    private Date date;
    @Column(nullable = false)
    private Boolean updated = false;
}
