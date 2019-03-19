package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.SyoboiOngoingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * @author firs
 */
@Repository
public interface SyoboiOngoingRepository extends JpaRepository<SyoboiOngoingEntity, Long> {
    List<SyoboiOngoingEntity> findByOngoingEntity_TidIn(List<Long> tids);

    Optional<SyoboiOngoingEntity> findByOngoingEntity_Tid(Long tid);
}
