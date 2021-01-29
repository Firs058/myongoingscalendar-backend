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
@Table(name = "syoboi_timetable",
        indexes = {
                @Index(columnList = "date_start", name = "syoboi_timetable_date_start_index"),
                @Index(columnList = "tid ASC, episode ASC, date_start ASC", name = "syoboi_timetable_tid_episode_date_start_index"),
                @Index(columnList = "tid ASC, episode ASC", name = "syoboi_timetable_tid_episode_index")
        }
)
public class SyoboiTimetableEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "syoboi_timetable_seq", sequenceName = "syoboi_timetable_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "syoboi_timetable_seq")
    private Long id;
    @JoinColumn(name = "tid", referencedColumnName = "tid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private OngoingEntity ongoingEntity;
    @JoinColumn(name = "ch", referencedColumnName = "id", nullable = false)
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private ChannelEntity channelEntity;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_start", columnDefinition = "timestamp with time zone", nullable = false)
    private Date dateStart;
    @Column(columnDefinition = "text", nullable = false)
    private String shift;
    @Column(nullable = false)
    private Integer episode;
    @Column(name = "episode_name", columnDefinition = "text", nullable = false)
    private String episodeName;
    @Transient
    private Boolean startedOnAir;

    @PostLoad
    private void onLoad() {
        this.startedOnAir = (this.episode != null && this.episode >= 1) && (this.dateStart != null && this.dateStart.before(new Date()));
    }
}
