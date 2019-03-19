package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.GenreEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface GenreService {
    Optional<GenreEntity> save(GenreEntity genreEntity);

    List<GenreEntity> saveAll(List<GenreEntity> genreEntityList);

    Optional<GenreEntity> get(Long id);

    Optional<GenreEntity> findByName(String name);

    List<GenreEntity> getAll();

    void delete(GenreEntity genreEntity);

    void deleteAll();
}
