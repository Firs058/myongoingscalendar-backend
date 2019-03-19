package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.ReportEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface ReportService {
    Optional<ReportEntity> save(ReportEntity reportEntity);

    List<ReportEntity> saveAll(List<ReportEntity> reportEntityList);

    Optional<ReportEntity> get(Long id);

    List<ReportEntity> getAll();

    Optional<ReportEntity> findByCommentEntity_OngoingEntity_TidAndCommentEntity_IdAndUserEntityId(Long tid, Long comment_id, Long user_id);

    void delete(ReportEntity reportEntity);

    void deleteAll();
}
