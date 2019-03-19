package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author firs
 */
@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    Optional<ReportEntity> findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(Long tid, Long comment_id, Long user_id);
}
