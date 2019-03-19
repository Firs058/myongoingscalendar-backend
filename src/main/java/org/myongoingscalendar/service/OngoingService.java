package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.OngoingEntity;
import org.myongoingscalendar.model.AdminData;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface OngoingService {
    Optional<OngoingEntity> save(OngoingEntity ongoingEntity);

    List<OngoingEntity> saveAll(List<OngoingEntity> ongoingEntityList);

    Optional<OngoingEntity> get(Long tid);

    Optional<OngoingEntity> findByTid(Long tid);

    List<OngoingEntity> findByTidIn(List<Long> tids);

    List<OngoingEntity> getAll();

    List<OngoingEntity> getCurrentOngoings();

    List<OngoingEntity> getCurrentOngoingsWithoutImage();

    List<OngoingEntity> getCurrentOngoingsWithoutVibrant();

    List<AdminData> getAdminData();

    void delete(OngoingEntity ongoingEntity);

    void deleteAll();

    void flush();

    void clearOngoingsCache();
}
