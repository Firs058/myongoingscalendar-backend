package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.BannedWordEntity;
import org.myongoingscalendar.repository.BannedWordRepository;
import org.myongoingscalendar.service.BannedWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class BannedWordServiceImpl implements BannedWordService {

    private final BannedWordRepository bannedWordRepository;

    @Autowired
    public BannedWordServiceImpl(BannedWordRepository bannedWordRepository) {
        this.bannedWordRepository = bannedWordRepository;
    }

    @Override
    public Optional<BannedWordEntity> save(BannedWordEntity bannedWordEntity) {
        return Optional.of(bannedWordRepository.save(bannedWordEntity));
    }

    @Override
    public List<BannedWordEntity> saveAll(List<BannedWordEntity> bannedWordEntityList) {
        return bannedWordRepository.saveAll(bannedWordEntityList);
    }

    @Override
    public Optional<BannedWordEntity> get(Long id) {
        return Optional.of(bannedWordRepository.getOne(id));
    }

    @Override
    public List<BannedWordEntity> getAll() {
        return bannedWordRepository.findAll();
    }

    @Override
    public void delete(BannedWordEntity bannedWordEntity) {
        bannedWordRepository.delete(bannedWordEntity);
    }

    @Override
    public void deleteAll() {
        bannedWordRepository.deleteAll();
    }
}
