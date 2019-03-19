package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.MalEntity;
import org.myongoingscalendar.repository.MalRepository;
import org.myongoingscalendar.service.MalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class MalServiceImpl implements MalService {

    private final MalRepository malRepository;

    @Autowired
    public MalServiceImpl(MalRepository malRepository) {
        this.malRepository = malRepository;
    }

    @Override
    public Optional<MalEntity> save(MalEntity malEntity) {
        return Optional.of(malRepository.save(malEntity));
    }

    @Override
    public List<MalEntity> saveAll(List<MalEntity> malEntityList) {
        return malRepository.saveAll(malEntityList);
    }

    @Override
    public Optional<MalEntity> get(Long id) {
        return Optional.of(malRepository.getOne(id));
    }

    @Override
    public List<MalEntity> getAll() {
        return malRepository.findAll();
    }

    @Override
    public void delete(MalEntity malEntity) {
        malRepository.delete(malEntity);
    }

    @Override
    public void deleteAll() {
        malRepository.deleteAll();
    }
}
