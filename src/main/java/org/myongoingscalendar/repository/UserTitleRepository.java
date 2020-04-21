package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.UserTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author firs
 */
@Repository
public interface UserTitleRepository extends JpaRepository<UserTitleEntity, Long> {
    @Query("select u.ongoingEntity.tid from UserTitleEntity u where u.ongoingEntity.tid in ?1 and u.userEntity.id = ?2")
    List<Long> getCurrentOngoingsAddedByUser(List<Long> ongoingEntities, Long userid);

    Boolean existsByOngoingEntity_Tid(Long tid);
}
