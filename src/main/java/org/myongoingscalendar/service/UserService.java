package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface UserService {
    Optional<UserEntity> save(UserEntity userEntity);

    List<UserEntity> saveAll(List<UserEntity> userEntityList);

    Optional<UserEntity> get(Long id);

    List<UserEntity> getAll();

    void delete(UserEntity userEntity);

    void deleteAll();

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByRecoverToken(String recoverToken);

    Optional<UserEntity> findByConfirmToken(String confirmToken);

    List<UserEntity> findByUserSettingsEntity_AvatarIsNull();
}
