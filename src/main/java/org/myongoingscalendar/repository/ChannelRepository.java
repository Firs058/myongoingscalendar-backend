package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author firs
 */
@Repository
public interface ChannelRepository extends JpaRepository<ChannelEntity, Long> {
    Optional<ChannelEntity> findByJa(String ja);
}
