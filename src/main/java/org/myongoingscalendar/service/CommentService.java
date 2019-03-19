package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.CommentEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface CommentService {
    Optional<CommentEntity> save(CommentEntity commentEntity);

    List<CommentEntity> saveAll(List<CommentEntity> commentEntityList);

    Optional<CommentEntity> get(Long id);

    List<CommentEntity> getAll();

    void delete(CommentEntity commentEntity);

    void deleteAll();
}
