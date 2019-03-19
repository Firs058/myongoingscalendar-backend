package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author firs
 */
@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
}
