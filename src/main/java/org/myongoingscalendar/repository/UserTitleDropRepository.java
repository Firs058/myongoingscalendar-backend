package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.UserTitleDropEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * @author firs
 */
@Repository
public interface UserTitleDropRepository extends JpaRepository<UserTitleDropEntity, Long> {
    @Query("select u.ongoingEntity.tid from UserTitleDropEntity u where u.ongoingEntity.tid in ?1 and u.userEntity.id = ?2")
    List<Long> getCurrentOngoingsTidsDroppedByUser(List<Long> ongoingEntities, Long userid);

    @Query("select u.ongoingEntity.tid from UserTitleDropEntity u where u.userEntity.id = ?1")
    List<Long> getAllOngoingsTidsDroppedByUser(Long userid);

    Optional<UserTitleDropEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);
}
