package org.myongoingscalendar.service.impl;


import org.myongoingscalendar.entity.UserTitleFavoriteEntity;
import org.myongoingscalendar.repository.UserTitleFavoriteRepository;
import org.myongoingscalendar.service.UserTitleFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class UserTitleFavoriteServiceImpl implements UserTitleFavoriteService {

    private final UserTitleFavoriteRepository userTitleFavoriteRepository;

    @Autowired
    public UserTitleFavoriteServiceImpl(UserTitleFavoriteRepository userTitleFavoriteRepository) {
        this.userTitleFavoriteRepository = userTitleFavoriteRepository;
    }

    @Override
    public Optional<UserTitleFavoriteEntity> save(UserTitleFavoriteEntity userTitleFavoriteEntity) {
        return Optional.of(userTitleFavoriteRepository.save(userTitleFavoriteEntity));
    }

    @Override
    public List<UserTitleFavoriteEntity> saveAll(List<UserTitleFavoriteEntity> userTitleFavoriteEntityList) {
        return userTitleFavoriteRepository.saveAll(userTitleFavoriteEntityList);
    }

    @Override
    public Optional<UserTitleFavoriteEntity> get(Long id) {
        return Optional.of(userTitleFavoriteRepository.getOne(id));
    }

    @Override
    public List<UserTitleFavoriteEntity> getAll() {
        return userTitleFavoriteRepository.findAll();
    }

    @Override
    public Optional<UserTitleFavoriteEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid) {
        return userTitleFavoriteRepository.findByOngoingEntity_TidAndUserEntity_Id(tid, userid);
    }

    @Override
    public List<Long> getOngoingsTidsFavoriteByUser(List<Long> tids, Long userid) {
        return userTitleFavoriteRepository.getOngoingsTidsFavoriteByUser(tids, userid);
    }

    @Override
    public List<Long> getAllOngoingsTidsFavoriteByUser(Long userid) {
        return userTitleFavoriteRepository.getAllOngoingsTidsFavoriteByUser(userid);
    }

    @Override
    public void delete(UserTitleFavoriteEntity userTitleFavoriteEntity) {
        userTitleFavoriteRepository.delete(userTitleFavoriteEntity);
    }

    @Override
    public void deleteAll() {
        userTitleFavoriteRepository.deleteAll();
    }
}
