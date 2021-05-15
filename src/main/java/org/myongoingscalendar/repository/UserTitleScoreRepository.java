package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.UserTitleScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * @author firs
 */
@Repository
public interface UserTitleScoreRepository extends JpaRepository<UserTitleScoreEntity, Long> {
    Optional<UserTitleScoreEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);

    List<UserTitleScoreEntity> findByUserEntity_Id(Long userid);
}
