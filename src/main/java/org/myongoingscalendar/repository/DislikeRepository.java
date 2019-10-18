package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.DislikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author firs
 */
@Repository
public interface DislikeRepository extends JpaRepository<DislikeEntity, Long> {
    Optional<DislikeEntity> findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(Long tid, Long comment_id);
}
