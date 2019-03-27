package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.UserEntity;
import org.myongoingscalendar.repository.UserRepository;
import org.myongoingscalendar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> save(UserEntity userEntity) {
        return Optional.of(userRepository.save(userEntity));
    }

    @Override
    public List<UserEntity> saveAll(List<UserEntity> userEntityList) {
        return userRepository.saveAll(userEntityList);
    }

    @Override
    public Optional<UserEntity> get(Long id) {
        return Optional.of(userRepository.getOne(id));
    }

    @Override
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(UserEntity userEntity) {
        userRepository.delete(userEntity);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public Optional<UserEntity> findByEmailContainingIgnoreCase(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email);
    }

    @Override
    public Optional<UserEntity> findByRecoverToken(String recoverToken) {
        return userRepository.findByRecoverToken(recoverToken);
    }

    @Override
    public Optional<UserEntity> findByConfirmToken(String confirmToken) {
        return userRepository.findByConfirmToken(confirmToken);
    }

    @Override
    public List<UserEntity> findByUserSettingsEntity_AvatarIsNull() {
        return userRepository.findByUserSettingsEntity_AvatarIsNull();
    }
}
