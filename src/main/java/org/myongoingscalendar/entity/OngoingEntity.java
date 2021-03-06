package org.myongoingscalendar.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
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
@Table(name = "ongoings", indexes = {
        @Index(columnList = "aid", name = "ongoings_aid_index"),
        @Index(columnList = "malid", name = "ongoings_malid_index")
})
public class OngoingEntity implements Serializable {
    @Id
    @Column(unique = true, nullable = false)
    private Long tid;
    @Column
    private Long aid;
    @Column
    private Long malid;
    @Column
    private Long annid;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ongoingEntity", orphanRemoval = true)
    private List<SyoboiTimetableEntity> syoboiTimetableEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ongoingEntity", orphanRemoval = true)
    private List<SyoboiRssEntity> syoboiRssEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ongoingEntity", orphanRemoval = true)
    private List<MalTitleGenreEntity> malTitleGenreEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ongoingEntity", orphanRemoval = true)
    private List<RatingEntity> ratingEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ongoingEntity", orphanRemoval = true)
    private List<UserTitleEntity> userTitleEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ongoingEntity", orphanRemoval = true)
    private List<UserTitleScoreEntity> userTitleScoreEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ongoingEntity", orphanRemoval = true)
    private List<UserTitleFavoriteEntity> userTitleFavoriteEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ongoingEntity", orphanRemoval = true)
    private List<CommentEntity> commentEntities;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "ongoingEntity", orphanRemoval = true)
    private List<NewsEntity> newsEntities;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "ongoingEntity", orphanRemoval = true)
    private SyoboiOngoingEntity syoboiOngoingEntity;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "ongoingEntity", orphanRemoval = true)
    private SyoboiInfoEntity syoboiInfoEntity;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "ongoingEntity", orphanRemoval = true)
    private AnidbEntity anidbEntity;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "ongoingEntity", orphanRemoval = true)
    private MalEntity malEntity;
}
