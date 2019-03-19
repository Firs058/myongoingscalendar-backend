package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.ChannelEntity;
import org.myongoingscalendar.repository.ChannelRepository;
import org.myongoingscalendar.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;

    @Autowired
    public ChannelServiceImpl(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Optional<ChannelEntity> save(ChannelEntity channelEntity) {
        return Optional.of(channelRepository.save(channelEntity));
    }

    @Override
    public Optional<ChannelEntity> findByJa(String ja) {
        return channelRepository.findByJa(ja);
    }

    @Override
    public List<ChannelEntity> saveAll(List<ChannelEntity> channelEntityList) {
        return channelRepository.saveAll(channelEntityList);
    }

    @Override
    public Optional<ChannelEntity> get(Long id) {
        return Optional.of(channelRepository.getOne(id));
    }

    @Override
    public List<ChannelEntity> getAll() {
        return channelRepository.findAll();
    }

    @Override
    public void delete(ChannelEntity channelEntity) {
        channelRepository.delete(channelEntity);
    }

    @Override
    public void deleteAll() {
        channelRepository.deleteAll();
    }
}
