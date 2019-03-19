package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.AnidbEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface AnidbService {
    Optional<AnidbEntity> save(AnidbEntity anidbEntity);

    List<AnidbEntity> saveAll(List<AnidbEntity> anidbEntityList);

    Optional<AnidbEntity> get(Long id);

    List<AnidbEntity> getAll();

    void delete(AnidbEntity anidbEntity);

    void deleteAll();
}
