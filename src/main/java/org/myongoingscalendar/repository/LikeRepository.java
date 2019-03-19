package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author firs
 */
@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(Long tid, Long comment_id, Long user_id);
}
