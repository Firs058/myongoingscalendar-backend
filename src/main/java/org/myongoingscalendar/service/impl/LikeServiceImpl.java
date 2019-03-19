package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.LikeEntity;
import org.myongoingscalendar.repository.LikeRepository;
import org.myongoingscalendar.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public Optional<LikeEntity> save(LikeEntity likeEntity) {
        return Optional.of(likeRepository.save(likeEntity));
    }

    @Override
    public List<LikeEntity> saveAll(List<LikeEntity> likeEntityList) {
        return likeRepository.saveAll(likeEntityList);
    }

    @Override
    public Optional<LikeEntity> get(Long id) {
        return Optional.of(likeRepository.getOne(id));
    }

    @Override
    public List<LikeEntity> getAll() {
        return likeRepository.findAll();
    }

    @Override
    public Optional<LikeEntity> findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(Long tid, Long comment_id, Long user_id) {
        return likeRepository.findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(tid, comment_id, user_id);
    }

    @Override
    public void delete(LikeEntity likeEntity) {
        likeRepository.delete(likeEntity);
    }

    @Override
    public void deleteAll() {
        likeRepository.deleteAll();
    }
}
