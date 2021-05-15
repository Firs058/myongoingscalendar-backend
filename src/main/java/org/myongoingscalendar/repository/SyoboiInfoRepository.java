package org.myongoingscalendar.repository;

import org.myongoingscalendar.entity.SyoboiInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author firs
 */
@Repository
public interface SyoboiInfoRepository extends JpaRepository<SyoboiInfoEntity, Long> {

    @Query("select min(s.firstYear) from SyoboiInfoEntity s where s.firstYear <> 0")
    Integer getMinYear();

    @Query("select max(s.firstYear) + 2 from SyoboiInfoEntity s where s.firstYear <> 0")
    Integer getMaxYear();

    List<SyoboiInfoEntity> findAllByFirstEndMonthIsNullAndFirstEndYearIsNull();
}
