package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.MalEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface MalService {
    Optional<MalEntity> save(MalEntity malEntity);

    List<MalEntity> saveAll(List<MalEntity> malEntityList);

    Optional<MalEntity> get(Long id);

    List<MalEntity> getAll();

    void delete(MalEntity malEntity);

    void deleteAll();
}
