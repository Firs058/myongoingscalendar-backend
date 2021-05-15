package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.OngoingEntity;
import org.myongoingscalendar.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author firs
 */
@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    List<RatingEntity> getAllByOngoingEntity(OngoingEntity ongoingEntity);
}
