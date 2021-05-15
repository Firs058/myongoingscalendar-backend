package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.UserTitleEntity;
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
public interface UserTitleRepository extends JpaRepository<UserTitleEntity, Long> {
    @Query("select u.ongoingEntity.tid from UserTitleEntity u where u.ongoingEntity.tid in ?1 and u.userEntity.id = ?2")
    List<Long> getCurrentOngoingsTidsAddedByUser(List<Long> ongoingEntities, Long userid);

    @Query("select u.ongoingEntity.tid from UserTitleEntity u where u.userEntity.id = ?1")
    List<Long> getAllOngoingsTidsAddedByUser(Long userid);

    Boolean existsByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);

    Optional<UserTitleEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);
}
