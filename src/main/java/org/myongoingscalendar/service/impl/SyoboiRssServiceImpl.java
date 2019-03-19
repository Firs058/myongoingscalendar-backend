package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.SyoboiRssEntity;
import org.myongoingscalendar.repository.SyoboiRssRepository;
import org.myongoingscalendar.service.SyoboiRssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class SyoboiRssServiceImpl implements SyoboiRssService {

    private final SyoboiRssRepository syoboiRssRepository;

    @Autowired
    public SyoboiRssServiceImpl(SyoboiRssRepository syoboiRssRepository) {
        this.syoboiRssRepository = syoboiRssRepository;
    }

    @Override
    public Optional<SyoboiRssEntity> save(SyoboiRssEntity syoboiRssEntity) {
        return Optional.of(syoboiRssRepository.save(syoboiRssEntity));
    }

    @Override
    public List<SyoboiRssEntity> saveAll(List<SyoboiRssEntity> syoboiRssEntityList) {
        return syoboiRssRepository.saveAll(syoboiRssEntityList);
    }

    @Override
    public Optional<SyoboiRssEntity> get(Long id) {
        return Optional.of(syoboiRssRepository.getOne(id));
    }

    @Override
    public List<SyoboiRssEntity> getAll() {
        return syoboiRssRepository.findAll();
    }

    @Override
    public void delete(SyoboiRssEntity syoboiRssEntity) {
        syoboiRssRepository.delete(syoboiRssEntity);
    }

    @Override
    public void deleteAll() {
        syoboiRssRepository.deleteAll();
    }
}
