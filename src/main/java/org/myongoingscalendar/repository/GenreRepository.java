package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author firs
 */
@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Long> {
    Optional<GenreEntity> findByName(String name);
}
