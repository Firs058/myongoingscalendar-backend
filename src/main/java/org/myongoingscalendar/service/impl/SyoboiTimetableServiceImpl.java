package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.SyoboiTimetableEntity;
import org.myongoingscalendar.repository.SyoboiTimetableRepository;
import org.myongoingscalendar.service.SyoboiTimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class SyoboiTimetableServiceImpl implements SyoboiTimetableService {

    private final SyoboiTimetableRepository syoboiTimetableRepository;

    @Autowired
    public SyoboiTimetableServiceImpl(SyoboiTimetableRepository syoboiTimetableRepository) {
        this.syoboiTimetableRepository = syoboiTimetableRepository;
    }

    @Override
    public Optional<SyoboiTimetableEntity> save(SyoboiTimetableEntity syoboiTimetableEntity) {
        return Optional.of(syoboiTimetableRepository.save(syoboiTimetableEntity));
    }

    @Override
    public List<SyoboiTimetableEntity> saveAll(List<SyoboiTimetableEntity> syoboiTimetableEntityList) {
        return syoboiTimetableRepository.saveAll(syoboiTimetableEntityList);
    }

    @Override
    public Optional<SyoboiTimetableEntity> get(Long id) {
        return Optional.of(syoboiTimetableRepository.getOne(id));
    }

    @Override
    public List<SyoboiTimetableEntity> getAll() {
        return syoboiTimetableRepository.findAll();
    }

    @Override
    public void delete(SyoboiTimetableEntity syoboiTimetableEntity) {
        syoboiTimetableRepository.delete(syoboiTimetableEntity);
    }

    @Override
    public void deleteAll() {
        syoboiTimetableRepository.deleteAll();
    }
}
