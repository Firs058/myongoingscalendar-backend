package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author firs
 */
@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {
}
