package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.OngoingEntity;
import org.myongoingscalendar.model.AdminData;
import org.myongoingscalendar.repository.OngoingRepository;
import org.myongoingscalendar.service.OngoingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author firs
 */
@Service
public class OngoingServiceImpl implements OngoingService {

    private final OngoingRepository ongoingRepository;

    @Autowired
    public OngoingServiceImpl(OngoingRepository ongoingRepository) {
        this.ongoingRepository = ongoingRepository;
    }

    @Override
    public Optional<OngoingEntity> save(OngoingEntity ongoingEntity) {
        return Optional.of(ongoingRepository.save(ongoingEntity));
    }

    @Override
    public List<OngoingEntity> saveAll(List<OngoingEntity> ongoingEntityList) {
        return ongoingRepository.saveAll(ongoingEntityList);
    }

    @Override
    public Optional<OngoingEntity> get(Long id) {
        return Optional.of(ongoingRepository.getOne(id));
    }

    @Override
    public Optional<OngoingEntity> findByTid(Long tid) {
        return ongoingRepository.findByTid(tid);
    }

    @Override
    public List<OngoingEntity> findByTidIn(List<Long> tids) {
        return ongoingRepository.findByTidIn(tids);
    }

    @Override
    public List<OngoingEntity> getAll() {
        return ongoingRepository.findAll();
    }

    @Override
    public List<OngoingEntity> getCurrentOngoings() {
        return ongoingRepository.getCurrentOngoings();
    }

    @Override
    public List<OngoingEntity> getCurrentOngoingsWithoutImage() {
        return ongoingRepository.getCurrentOngoingsWithoutImage();
    }

    @Override
    public List<OngoingEntity> getCurrentOngoingsWithoutVibrant() {
        return ongoingRepository.getCurrentOngoingsWithoutVibrant();
    }

    @Override
    public List<AdminData> getAdminData() {
        return ongoingRepository.getAdminData().stream().map(o ->
                new AdminData()
                        .tid(Objects.nonNull(o.tid()) ? o.tid() : null)
                        .malid(Objects.nonNull(o.malid()) ? o.malid() : null)
                        .aid(Objects.nonNull(o.aid()) ? o.aid() : null)
                        .titleen(Objects.nonNull(o.anidbEntity()) ? o.anidbEntity().titleEN() : "null")
                        .title(Objects.nonNull(o.syoboiInfoEntity()) ? o.syoboiInfoEntity().title() : "null"))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(OngoingEntity ongoingEntity) {
        ongoingRepository.delete(ongoingEntity);
    }

    @Override
    public void deleteAll() {
        ongoingRepository.deleteAll();
    }

    @Override
    public void flush() {
        ongoingRepository.flush();
    }

    @Override
    @CacheEvict(value = {"getCurrentOngoingsList", "getAllOngoingsList"}, allEntries = true)
    public void clearOngoingsCache() {
    }
}
