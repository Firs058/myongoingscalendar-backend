package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.UserTitleDropEntity;
import org.myongoingscalendar.repository.UserTitleDropRepository;
import org.myongoingscalendar.service.UserTitleDropService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class UserTitleDropServiceImpl implements UserTitleDropService {

    private final UserTitleDropRepository userTitleDropRepository;

    public UserTitleDropServiceImpl(UserTitleDropRepository userTitleDropRepository) {
        this.userTitleDropRepository = userTitleDropRepository;
    }

    @Override
    public Optional<UserTitleDropEntity> save(UserTitleDropEntity userTitleDropEntity) {
        return Optional.of(userTitleDropRepository.save(userTitleDropEntity));
    }

    @Override
    public List<UserTitleDropEntity> saveAll(List<UserTitleDropEntity> userTitleDropEntityList) {
        return userTitleDropRepository.saveAll(userTitleDropEntityList);
    }

    @Override
    public Optional<UserTitleDropEntity> get(Long id) {
        return Optional.of(userTitleDropRepository.getOne(id));
    }

    @Override
    public List<UserTitleDropEntity> getAll() {
        return userTitleDropRepository.findAll();
    }

    @Override
    public List<Long> getCurrentOngoingsDroppedByUser(List<Long> ongoingEntities, Long userid) {
        return userTitleDropRepository.getCurrentOngoingsDroppedByUser(ongoingEntities, userid);
    }

    @Override
    public Optional<UserTitleDropEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid) {
        return userTitleDropRepository.findByOngoingEntity_TidAndUserEntity_Id(tid, userid);
    }

    @Override
    public void delete(UserTitleDropEntity userTitleDropEntity) {
        userTitleDropRepository.delete(userTitleDropEntity);
    }

    @Override
    public void deleteAll() {
        userTitleDropRepository.deleteAll();
    }
}
