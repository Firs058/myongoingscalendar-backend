package org.myongoingscalendar.service;


import org.myongoingscalendar.entity.UserTitleFavoriteEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface UserTitleFavoriteService {
    Optional<UserTitleFavoriteEntity> save(UserTitleFavoriteEntity userTitleFavoriteEntity);

    List<UserTitleFavoriteEntity> saveAll(List<UserTitleFavoriteEntity> userTitleFavoriteEntityList);

    Optional<UserTitleFavoriteEntity> get(Long id);

    List<UserTitleFavoriteEntity> getAll();

    Optional<UserTitleFavoriteEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);

    List<Long> getOngoingsTidsFavoriteByUser(List<Long> tids, Long userid);

    List<Long> getAllOngoingsTidsFavoriteByUser(Long userid);

    void delete(UserTitleFavoriteEntity userTitleFavoriteEntity);

    void deleteAll();
}
