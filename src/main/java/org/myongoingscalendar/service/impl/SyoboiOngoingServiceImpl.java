package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.SyoboiOngoingEntity;
import org.myongoingscalendar.repository.SyoboiOngoingRepository;
import org.myongoingscalendar.service.SyoboiOngoingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class SyoboiOngoingServiceImpl implements SyoboiOngoingService {

    private final SyoboiOngoingRepository syoboiOngoingRepository;

    @Autowired
    public SyoboiOngoingServiceImpl(SyoboiOngoingRepository syoboiOngoingRepository) {
        this.syoboiOngoingRepository = syoboiOngoingRepository;
    }

    @Override
    public Optional<SyoboiOngoingEntity> save(SyoboiOngoingEntity syoboiOngoingEntity) {
        return Optional.of(syoboiOngoingRepository.save(syoboiOngoingEntity));
    }

    @Override
    public List<SyoboiOngoingEntity> saveAll(List<SyoboiOngoingEntity> syoboiOngoingEntityList) {
        return syoboiOngoingRepository.saveAll(syoboiOngoingEntityList);
    }

    @Override
    public Optional<SyoboiOngoingEntity> get(Long id) {
        return Optional.of(syoboiOngoingRepository.getOne(id));
    }

    @Override
    public List<SyoboiOngoingEntity> getAll() {
        return syoboiOngoingRepository.findAll();
    }

    @Override
    public Optional<SyoboiOngoingEntity> findByOngoingEntity_Tid(Long tid) {
        return syoboiOngoingRepository.findByOngoingEntity_Tid(tid);
    }

    @Override
    public List<SyoboiOngoingEntity> findByOngoingEntity_TidIn(List<Long> tids) {
        return syoboiOngoingRepository.findByOngoingEntity_TidIn(tids);
    }

    @Override
    public void delete(SyoboiOngoingEntity syoboiOngoingEntity) {
        syoboiOngoingRepository.delete(syoboiOngoingEntity);
    }

    @Override
    public void deleteAll() {
        syoboiOngoingRepository.deleteAll();
    }

    @Override
    public void flush() {
        syoboiOngoingRepository.flush();
    }
}
