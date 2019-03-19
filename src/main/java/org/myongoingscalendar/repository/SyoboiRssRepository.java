package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.SyoboiRssEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author firs
 */
@Repository
public interface SyoboiRssRepository extends JpaRepository<SyoboiRssEntity, Long> {
}
