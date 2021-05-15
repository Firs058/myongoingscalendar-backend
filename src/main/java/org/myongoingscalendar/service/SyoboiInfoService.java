package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.SyoboiInfoEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface SyoboiInfoService {
    Optional<SyoboiInfoEntity> save(SyoboiInfoEntity syoboiInfoEntity);

    List<SyoboiInfoEntity> saveAll(List<SyoboiInfoEntity> syoboiInfoEntityList);

    Optional<SyoboiInfoEntity> get(Long id);

    List<SyoboiInfoEntity> getAll();

    List<SyoboiInfoEntity> findAllByFirstEndMonthIsNullAndFirstEndYearIsNull();

    void delete(SyoboiInfoEntity syoboiInfoEntity);

    void deleteAll();

    List<Integer> getYearsRanges();
}
