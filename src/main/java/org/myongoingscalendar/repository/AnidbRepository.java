package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.AnidbEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author firs
 */
@Repository
public interface AnidbRepository extends JpaRepository<AnidbEntity, Long> {
}
