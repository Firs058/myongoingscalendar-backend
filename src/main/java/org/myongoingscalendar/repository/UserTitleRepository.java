package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.UserTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author firs
 */
@Repository
public interface UserTitleRepository extends JpaRepository<UserTitleEntity, Long> {
    List<UserTitleEntity> findByOngoingEntity_TidInAndUserEntity_Id(List<Long> ongoingEntities, Long userid);
}
