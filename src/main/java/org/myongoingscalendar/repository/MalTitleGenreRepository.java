package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.MalTitleGenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author firs
 */
@Repository
public interface MalTitleGenreRepository extends JpaRepository<MalTitleGenreEntity, Long> {
}
