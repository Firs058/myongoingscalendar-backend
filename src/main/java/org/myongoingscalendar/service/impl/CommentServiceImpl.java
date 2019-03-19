package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.CommentEntity;
import org.myongoingscalendar.repository.CommentRepository;
import org.myongoingscalendar.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Optional<CommentEntity> save(CommentEntity commentEntity) {
        return Optional.of(commentRepository.save(commentEntity));
    }

    @Override
    public List<CommentEntity> saveAll(List<CommentEntity> commentEntityList) {
        return commentRepository.saveAll(commentEntityList);
    }

    @Override
    public Optional<CommentEntity> get(Long id) {
        return Optional.of(commentRepository.getOne(id));
    }

    @Override
    public List<CommentEntity> getAll() {
        return commentRepository.findAll();
    }

    @Override
    public void delete(CommentEntity commentEntity) {
        commentRepository.delete(commentEntity);
    }

    @Override
    public void deleteAll() {
        commentRepository.deleteAll();
    }
}
