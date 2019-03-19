package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.SyoboiOngoingEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface SyoboiOngoingService {
    Optional<SyoboiOngoingEntity> save(SyoboiOngoingEntity syoboiOngoingEntity);

    List<SyoboiOngoingEntity> saveAll(List<SyoboiOngoingEntity> syoboiOngoingEntityList);

    Optional<SyoboiOngoingEntity> get(Long id);

    List<SyoboiOngoingEntity> getAll();

    Optional<SyoboiOngoingEntity> findByOngoingEntity_Tid(Long tid);

    List<SyoboiOngoingEntity> findByOngoingEntity_TidIn(List<Long> tids);

    void delete(SyoboiOngoingEntity syoboiOngoingEntity);

    void deleteAll();

    void flush();
}
