package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.BannedWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author firs
 */
@Repository
public interface BannedWordRepository extends JpaRepository<BannedWordEntity, Long> {
}
