package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.UserTitleScoreEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface UserTitleScoreService {
    Optional<UserTitleScoreEntity> save(UserTitleScoreEntity userTitleScoreEntity);

    List<UserTitleScoreEntity> saveAll(List<UserTitleScoreEntity> userTitleScoreEntityList);

    Optional<UserTitleScoreEntity> get(Long id);

    List<UserTitleScoreEntity> getAll();

    Optional<UserTitleScoreEntity> findByOngoingEntity_TidAndUserEntity_Id(Long tid, Long userid);

    List<UserTitleScoreEntity> findByUserEntity_Id(Long userid);

    void delete(UserTitleScoreEntity userTitleScoreEntity);

    void deleteAll();
}
