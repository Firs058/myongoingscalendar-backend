package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.LikeEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface LikeService {
    Optional<LikeEntity> save(LikeEntity likeEntity);

    List<LikeEntity> saveAll(List<LikeEntity> likeEntityList);

    Optional<LikeEntity> get(Long id);

    List<LikeEntity> getAll();

    Optional<LikeEntity> findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(Long tid, Long comment_id, Long user_id);

    void delete(LikeEntity likeEntity);

    void deleteAll();
}
