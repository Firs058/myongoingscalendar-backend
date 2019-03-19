package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.BannedWordEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface BannedWordService {
    Optional<BannedWordEntity> save(BannedWordEntity bannedWordEntity);

    List<BannedWordEntity> saveAll(List<BannedWordEntity> bannedWordEntityList);

    Optional<BannedWordEntity> get(Long id);

    List<BannedWordEntity> getAll();

    void delete(BannedWordEntity bannedWordEntity);

    void deleteAll();
}
