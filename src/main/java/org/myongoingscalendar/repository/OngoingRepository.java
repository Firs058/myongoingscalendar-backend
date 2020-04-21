package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.OngoingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Repository
public interface OngoingRepository extends JpaRepository<OngoingEntity, Long> {
    Optional<OngoingEntity> findByTid(Long tid);

    Optional<OngoingEntity> findByAnnid(Long tid);

    List<OngoingEntity> findByTidIn(List<Long> tids);

    List<OngoingEntity> findByMalidIsNotNull();

    List<OngoingEntity> findByAidIsNotNull();

    List<OngoingEntity> findAllByAidIsNotNullOrMalidIsNotNullOrAnnidIsNotNull();

    @Query("select o from OngoingEntity o join o.syoboiOngoingEntity s where s is not null")
    List<OngoingEntity> getCurrentOngoings();

    @Query("select o.tid from OngoingEntity o join o.syoboiOngoingEntity s where s is not null")
    List<Long> getCurrentOngoingsTids();

    @Query("select o from OngoingEntity o join o.anidbEntity s where s is not null and s.image = false")
    List<OngoingEntity> getCurrentOngoingsWithoutImage();

    @Query("select o from OngoingEntity o join o.anidbEntity s where s is not null and s.vibrant is null")
    List<OngoingEntity> getCurrentOngoingsWithoutVibrant();

    @Query("select o from OngoingEntity o join o.syoboiOngoingEntity s where o.tid not in (select e.tid from OngoingEntity e join e.syoboiInfoEntity i)")
    List<OngoingEntity> getCurrentOngoingsWithoutInfo();
}
