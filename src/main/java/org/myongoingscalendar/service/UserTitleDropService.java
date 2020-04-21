package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.UserTitleDropEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface UserTitleDropService {
    Optional<UserTitleDropEntity> save(UserTitleDropEntity userTitleDropEntity);

    List<UserTitleDropEntity> saveAll(List<UserTitleDropEntity> userTitleDropEntityList);

    Optional<UserTitleDropEntity> get(Long id);

    List<UserTitleDropEntity> getAll();

    List<Long> getCurrentOngoingsTidsDroppedByUser(List<Long> ongoingEntities, Long userid);

    Optional<UserTitleDropEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);

    void delete(UserTitleDropEntity userTitleDropEntity);

    void deleteAll();
}
