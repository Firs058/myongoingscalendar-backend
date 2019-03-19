package org.myongoingscalendar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author firs
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Entity
@Table(name = "comments")
public class CommentEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    @SequenceGenerator(name = "comments_seq", sequenceName = "comments_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_seq")
    private Long id;
    @Column(columnDefinition = "ltree", nullable = false)
    @Type(type = "org.myongoingscalendar.model.LTreeType")
    private String path;
    @JoinColumn(name = "tid", referencedColumnName = "tid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private OngoingEntity ongoingEntity;
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userEntity;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "commentEntity", orphanRemoval = true)
    private List<LikeEntity> likeEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "commentEntity", orphanRemoval = true)
    private List<DislikeEntity> dislikeEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "commentEntity", orphanRemoval = true)
    private List<ReportEntity> reportEntities;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "timestamp with time zone", nullable = false)
    private Date added;
    @Column(columnDefinition = "text", nullable = false)
    private String text;
}
