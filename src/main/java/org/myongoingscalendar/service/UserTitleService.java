package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.UserTitleEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface UserTitleService {
    Optional<UserTitleEntity> save(UserTitleEntity userTitleEntity);

    List<UserTitleEntity> saveAll(List<UserTitleEntity> userTitleEntityList);

    Optional<UserTitleEntity> get(Long id);

    Boolean existsByOngoingEntity_Tid(Long tid);

    List<UserTitleEntity> getAll();

    List<Long> getCurrentOngoingsTidsAddedByUser(List<Long> ongoingEntities, Long userid);

    void delete(UserTitleEntity userTitleEntity);

    void deleteAll();
}
