package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.DislikeEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface DislikeService {
    Optional<DislikeEntity> save(DislikeEntity dislikeEntity);

    List<DislikeEntity> saveAll(List<DislikeEntity> dislikeEntityList);

    Optional<DislikeEntity> get(Long id);

    List<DislikeEntity> getAll();

    Optional<DislikeEntity> findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(Long tid, Long comment_id);

    void delete(DislikeEntity dislikeEntity);

    void deleteAll();
}
