package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.SyoboiRssEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface SyoboiRssService {
    Optional<SyoboiRssEntity> save(SyoboiRssEntity syoboiRssEntity);

    List<SyoboiRssEntity> saveAll(List<SyoboiRssEntity> syoboiRssEntityList);

    Optional<SyoboiRssEntity> get(Long id);

    List<SyoboiRssEntity> getAll();

    void delete(SyoboiRssEntity syoboiRssEntity);

    void deleteAll();
}
