package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.MalTitleGenreEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface MalTitleGenreService {
    Optional<MalTitleGenreEntity> save(MalTitleGenreEntity malTitleGenreEntity);

    List<MalTitleGenreEntity> saveAll(List<MalTitleGenreEntity> malTitleGenreEntityList);

    Optional<MalTitleGenreEntity> get(Long id);

    List<MalTitleGenreEntity> getAll();

    void delete(MalTitleGenreEntity malTitleGenreEntity);

    void deleteAll();
}
