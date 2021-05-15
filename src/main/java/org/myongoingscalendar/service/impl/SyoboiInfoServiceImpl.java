package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.SyoboiInfoEntity;
import org.myongoingscalendar.repository.SyoboiInfoRepository;
import org.myongoingscalendar.service.SyoboiInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class SyoboiInfoServiceImpl implements SyoboiInfoService {

    private final SyoboiInfoRepository syoboiInfoRepository;

    @Autowired
    public SyoboiInfoServiceImpl(SyoboiInfoRepository syoboiInfoRepository) {
        this.syoboiInfoRepository = syoboiInfoRepository;
    }

    @Override
    public Optional<SyoboiInfoEntity> save(SyoboiInfoEntity syoboiInfoEntity) {
        return Optional.of(syoboiInfoRepository.save(syoboiInfoEntity));
    }

    @Override
    public List<SyoboiInfoEntity> saveAll(List<SyoboiInfoEntity> syoboiInfoEntityList) {
        return syoboiInfoRepository.saveAll(syoboiInfoEntityList);
    }

    @Override
    public Optional<SyoboiInfoEntity> get(Long id) {
        return Optional.of(syoboiInfoRepository.getOne(id));
    }

    @Override
    public List<SyoboiInfoEntity> getAll() {
        return syoboiInfoRepository.findAll();
    }

    @Override
    public List<SyoboiInfoEntity> findAllByFirstEndMonthIsNullAndFirstEndYearIsNull() {
        return syoboiInfoRepository.findAllByFirstEndMonthIsNullAndFirstEndYearIsNull();
    }

    @Override
    public void delete(SyoboiInfoEntity syoboiInfoEntity) {
        syoboiInfoRepository.delete(syoboiInfoEntity);
    }

    @Override
    public void deleteAll() {
        syoboiInfoRepository.deleteAll();
    }

    @Override
    @Cacheable("getYearsRanges")
    public List<Integer> getYearsRanges() {
        return Arrays.asList(syoboiInfoRepository.getMinYear(), syoboiInfoRepository.getMaxYear());
    }
}
