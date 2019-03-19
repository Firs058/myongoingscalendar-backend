package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.SyoboiTimetableEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface SyoboiTimetableService {
    Optional<SyoboiTimetableEntity> save(SyoboiTimetableEntity syoboiTimetableEntity);

    List<SyoboiTimetableEntity> saveAll(List<SyoboiTimetableEntity> syoboiTimetableEntityList);

    Optional<SyoboiTimetableEntity> get(Long id);

    List<SyoboiTimetableEntity> getAll();

    void delete(SyoboiTimetableEntity syoboiTimetableEntity);

    void deleteAll();
}
