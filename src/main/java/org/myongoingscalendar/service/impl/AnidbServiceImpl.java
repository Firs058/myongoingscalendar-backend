package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.AnidbEntity;
import org.myongoingscalendar.repository.AnidbRepository;
import org.myongoingscalendar.service.AnidbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class AnidbServiceImpl implements AnidbService {

    private final AnidbRepository anidbRepository;

    @Autowired
    public AnidbServiceImpl(AnidbRepository anidbRepository) {
        this.anidbRepository = anidbRepository;
    }

    @Override
    public Optional<AnidbEntity> save(AnidbEntity anidbEntity) {
        return Optional.of(anidbRepository.save(anidbEntity));
    }

    @Override
    public List<AnidbEntity> saveAll(List<AnidbEntity> anidbEntityList) {
        return anidbRepository.saveAll(anidbEntityList);
    }

    @Override
    public Optional<AnidbEntity> get(Long id) {
        return Optional.of(anidbRepository.getOne(id));
    }

    @Override
    public List<AnidbEntity> getAll() {
        return anidbRepository.findAll();
    }

    @Override
    public void delete(AnidbEntity anidbEntity) {
        anidbRepository.delete(anidbEntity);
    }

    @Override
    public void deleteAll() {
        anidbRepository.deleteAll();
    }
}
