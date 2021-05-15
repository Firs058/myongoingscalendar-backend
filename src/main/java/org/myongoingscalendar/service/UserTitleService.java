package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.UserTitleEntity;
import org.myongoingscalendar.entity.UserTitleFavoriteEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface UserTitleService {
    Optional<UserTitleEntity> save(UserTitleEntity userTitleEntity);

    List<UserTitleEntity> saveAll(List<UserTitleEntity> userTitleEntityList);

    Optional<UserTitleEntity> get(Long id);

    Boolean existsByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);

    List<UserTitleEntity> getAll();

    List<Long> getAllOngoingsTidsAddedByUser(Long userid);

    List<Long> getCurrentOngoingsTidsAddedByUser(List<Long> ongoingEntities, Long userid);

    Optional<UserTitleEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);

    void delete(UserTitleEntity userTitleEntity);

    void deleteAll();
}
