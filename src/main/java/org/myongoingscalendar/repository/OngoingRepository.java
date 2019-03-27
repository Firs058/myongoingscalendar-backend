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

    List<OngoingEntity> findByTidIn(List<Long> tids);

    @Query("select o from OngoingEntity o join o.syoboiOngoingEntity s where s is not null")
    List<OngoingEntity> getCurrentOngoings();

    @Query("select o from OngoingEntity o join o.anidbEntity s where s is not null and s.image = false")
    List<OngoingEntity> getCurrentOngoingsWithoutImage();

    @Query("select o from OngoingEntity o join o.anidbEntity s where s is not null and s.vibrant is null")
    List<OngoingEntity> getCurrentOngoingsWithoutVibrant();

    @Query("select o from OngoingEntity o join o.syoboiOngoingEntity s where o.malid is null or o.aid is null")
    List<OngoingEntity> getAdminData();
}
