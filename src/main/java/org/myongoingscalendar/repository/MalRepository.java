package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.MalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author firs
 */
@Repository
public interface MalRepository extends JpaRepository<MalEntity, Long> {
}
