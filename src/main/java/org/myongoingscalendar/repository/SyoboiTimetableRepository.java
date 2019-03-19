package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.SyoboiTimetableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author firs
 */
@Repository
public interface SyoboiTimetableRepository extends JpaRepository<SyoboiTimetableEntity, Long> {
}
