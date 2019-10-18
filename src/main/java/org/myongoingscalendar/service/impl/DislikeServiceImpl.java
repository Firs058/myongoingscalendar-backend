package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.DislikeEntity;
import org.myongoingscalendar.repository.DislikeRepository;
import org.myongoingscalendar.service.DislikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class DislikeServiceImpl implements DislikeService {

    private final DislikeRepository dislikeRepository;

    @Autowired
    public DislikeServiceImpl(DislikeRepository dislikeRepository) {
        this.dislikeRepository = dislikeRepository;
    }

    @Override
    public Optional<DislikeEntity> save(DislikeEntity dislikeEntity) {
        return Optional.of(dislikeRepository.save(dislikeEntity));
    }

    @Override
    public List<DislikeEntity> saveAll(List<DislikeEntity> dislikeEntityList) {
        return dislikeRepository.saveAll(dislikeEntityList);
    }

    @Override
    public Optional<DislikeEntity> get(Long id) {
        return Optional.of(dislikeRepository.getOne(id));
    }

    @Override
    public List<DislikeEntity> getAll() {
        return dislikeRepository.findAll();
    }

    @Override
    public Optional<DislikeEntity> findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(Long tid, Long comment_id) {
        return dislikeRepository.findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(tid, comment_id);
    }

    @Override
    public void delete(DislikeEntity dislikeEntity) {
        dislikeRepository.delete(dislikeEntity);
    }

    @Override
    public void deleteAll() {
        dislikeRepository.deleteAll();
    }
}
