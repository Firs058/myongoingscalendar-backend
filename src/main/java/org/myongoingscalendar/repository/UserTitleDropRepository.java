package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.UserTitleDropEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * @author firs
 */
@Repository
public interface UserTitleDropRepository extends JpaRepository<UserTitleDropEntity, Long> {
    List<UserTitleDropEntity> findByOngoingEntity_TidInAndUserEntity_Id(List<Long> ongoingEntities, Long userid);
    Optional<UserTitleDropEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);
}
