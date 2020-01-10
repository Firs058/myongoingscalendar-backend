package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.UserTitleDropEntity;
import org.myongoingscalendar.entity.UserTitleEntity;
import org.myongoingscalendar.model.Status;
import org.myongoingscalendar.service.OngoingService;
import org.myongoingscalendar.service.UserService;
import org.myongoingscalendar.service.UserServiceCustom;
import org.myongoingscalendar.service.UserTitleDropService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author firs
 */
@Service
public class UserServiceCustomImpl implements UserServiceCustom {

    private final UserService userService;
    private final OngoingService ongoingService;
    private final UserTitleDropService userTitleDropService;

    public UserServiceCustomImpl(UserService userService, OngoingService ongoingService, UserTitleDropService userTitleDropService) {
        this.userService = userService;
        this.ongoingService = ongoingService;
        this.userTitleDropService = userTitleDropService;
    }

    @Override
    @Transactional
    public Status toggleUserTid(Long tid, Long user_id) {
        return userService.get(user_id)
                .map(userEntity ->
                        ongoingService.findByTid(tid)
                                .map(ongoingEntity -> {
                                    if (ongoingEntity.syoboiInfoEntity().outdated() && ongoingEntity.syoboiOngoingEntity() == null)
                                        return new Status(10033, "You can't interact with outdated anime");

                                    UserTitleEntity userTitleEntity = new UserTitleEntity()
                                            .ongoingEntity(ongoingEntity)
                                            .userEntity(userEntity);

                                    return userEntity.usersTitleEntities().stream()
                                            .filter(u -> u.ongoingEntity().equals(userTitleEntity.ongoingEntity()) && u.userEntity().equals(userTitleEntity.userEntity()))
                                            .findAny()
                                            .map(f -> {
                                                userEntity.usersTitleEntities().remove(f);
                                                userEntity.userTitleDropEntities().add(new UserTitleDropEntity().userEntity(userEntity).ongoingEntity(ongoingEntity).dateAdded(f.dateAdded()));
                                                userService.save(userEntity);
                                                return new Status(11007, "Title removed");
                                            })
                                            .orElseGet(() -> {
                                                userTitleDropService.findByOngoingEntity_TidAndUserEntity_Id(tid, user_id).ifPresent(d -> userEntity.userTitleDropEntities().remove(d));
                                                userEntity.usersTitleEntities().add(userTitleEntity);
                                                userService.save(userEntity);
                                                return new Status(11008, "Title added");
                                            });
                                })
                                .orElse(new Status(10018, "Not found")))
                .orElse(new Status(10012, "You must be logged"));
    }
}
