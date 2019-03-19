package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.ChannelEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface ChannelService {
    Optional<ChannelEntity> save(ChannelEntity channelEntity);

    List<ChannelEntity> saveAll(List<ChannelEntity> channelEntityList);

    Optional<ChannelEntity> get(Long id);

    Optional<ChannelEntity> findByJa(String ja);

    List<ChannelEntity> getAll();

    void delete(ChannelEntity channelEntity);

    void deleteAll();
}
