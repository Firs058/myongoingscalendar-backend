package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.UserTitleFavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * @author firs
 */
@Repository
public interface UserTitleFavoriteRepository extends JpaRepository<UserTitleFavoriteEntity, Long> {
    Optional<UserTitleFavoriteEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);

    @Query("select u.ongoingEntity.tid from UserTitleFavoriteEntity u where u.ongoingEntity.tid in ?1 and u.userEntity.id = ?2")
    List<Long> getOngoingsTidsFavoriteByUser(List<Long> tids, Long userid);

    @Query("select u.ongoingEntity.tid from UserTitleFavoriteEntity u where u.userEntity.id = ?1")
    List<Long> getAllOngoingsTidsFavoriteByUser(Long userid);
}
