package org.myongoingscalendar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * @author firs
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Entity
@Table(name = "news")
public class NewsEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "news_seq", sequenceName = "news_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_seq")
    private Long id;
    @JoinColumn(name = "tid", referencedColumnName = "tid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private OngoingEntity ongoingEntity;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp with time zone", nullable = false)
    private Date datetime;
    @Column(columnDefinition = "text", nullable = false)
    private String href;
    @Column(columnDefinition = "text", nullable = false)
    private String headline;
    @Column(columnDefinition = "text", nullable = false)
    private Locale lang;
    @Column(columnDefinition = "text", nullable = false)
    private String source;
}
