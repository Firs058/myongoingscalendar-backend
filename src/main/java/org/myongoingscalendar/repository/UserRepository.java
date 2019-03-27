package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailContainingIgnoreCase(String email);

    Optional<UserEntity> findByRecoverToken(String recoverToken);

    Optional<UserEntity> findByConfirmToken(String confirmToken);

    List<UserEntity> findByUserSettingsEntity_AvatarIsNull();
}
