package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.UserTitleScoreEntity;
import org.myongoingscalendar.repository.UserTitleScoreRepository;
import org.myongoingscalendar.service.UserTitleScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class UserTitleScoreServiceImpl implements UserTitleScoreService {

    private final UserTitleScoreRepository userTitleScoreRepository;

    @Autowired
    public UserTitleScoreServiceImpl(UserTitleScoreRepository userTitleScoreRepository) {
        this.userTitleScoreRepository = userTitleScoreRepository;
    }

    @Override
    public Optional<UserTitleScoreEntity> save(UserTitleScoreEntity userTitleScoreEntity) {
        return Optional.of(userTitleScoreRepository.save(userTitleScoreEntity));
    }

    @Override
    public List<UserTitleScoreEntity> saveAll(List<UserTitleScoreEntity> userTitleScoreEntityList) {
        return userTitleScoreRepository.saveAll(userTitleScoreEntityList);
    }

    @Override
    public Optional<UserTitleScoreEntity> get(Long id) {
        return Optional.of(userTitleScoreRepository.getOne(id));
    }

    @Override
    public List<UserTitleScoreEntity> getAll() {
        return userTitleScoreRepository.findAll();
    }

    @Override
    public Optional<UserTitleScoreEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid) {
        return userTitleScoreRepository.findByOngoingEntity_TidAndUserEntity_Id(tid, userid);
    }

    @Override
    public List<UserTitleScoreEntity> findByUserEntity_Id(Long userid) {
        return userTitleScoreRepository.findByUserEntity_Id(userid);
    }

    @Override
    public void delete(UserTitleScoreEntity userTitleScoreEntity) {
        userTitleScoreRepository.delete(userTitleScoreEntity);
    }

    @Override
    public void deleteAll() {
        userTitleScoreRepository.deleteAll();
    }
}
