package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.UserTitleEntity;
import org.myongoingscalendar.entity.UserTitleFavoriteEntity;
import org.myongoingscalendar.repository.UserTitleRepository;
import org.myongoingscalendar.service.UserTitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class UserTitleServiceImpl implements UserTitleService {

    private final UserTitleRepository userTitleRepository;

    @Autowired
    public UserTitleServiceImpl(UserTitleRepository userTitleRepository) {
        this.userTitleRepository = userTitleRepository;
    }

    @Override
    public Optional<UserTitleEntity> save(UserTitleEntity userTitleEntity) {
        return Optional.of(userTitleRepository.save(userTitleEntity));
    }

    @Override
    public List<UserTitleEntity> saveAll(List<UserTitleEntity> userTitleEntityList) {
        return userTitleRepository.saveAll(userTitleEntityList);
    }

    @Override
    public Optional<UserTitleEntity> get(Long id) {
        return Optional.of(userTitleRepository.getOne(id));
    }

    @Override
    public Boolean existsByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid) {
        return userTitleRepository.existsByOngoingEntity_TidAndUserEntity_Id(tid, userid);
    }

    @Override
    public List<UserTitleEntity> getAll() {
        return userTitleRepository.findAll();
    }

    @Override
    public List<Long> getAllOngoingsTidsAddedByUser(Long userid) {
        return userTitleRepository.getAllOngoingsTidsAddedByUser(userid);
    }

    @Override
    public List<Long> getCurrentOngoingsTidsAddedByUser(List<Long> ongoingEntities, Long userid) {
        return userTitleRepository.getCurrentOngoingsTidsAddedByUser(ongoingEntities, userid);
    }

    @Override
    public Optional<UserTitleEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid) {
        return userTitleRepository.findByOngoingEntity_TidAndUserEntity_Id(tid, userid);
    }

    @Override
    public void delete(UserTitleEntity userTitleEntity) {
        userTitleRepository.delete(userTitleEntity);
    }

    @Override
    public void deleteAll() {
        userTitleRepository.deleteAll();
    }
}
