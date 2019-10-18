package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.ReportEntity;
import org.myongoingscalendar.repository.ReportRepository;
import org.myongoingscalendar.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Optional<ReportEntity> save(ReportEntity reportEntity) {
        return Optional.of(reportRepository.save(reportEntity));
    }

    @Override
    public List<ReportEntity> saveAll(List<ReportEntity> reportEntityList) {
        return reportRepository.saveAll(reportEntityList);
    }

    @Override
    public Optional<ReportEntity> get(Long id) {
        return Optional.of(reportRepository.getOne(id));
    }

    @Override
    public List<ReportEntity> getAll() {
        return reportRepository.findAll();
    }

    @Override
    public Optional<ReportEntity> findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(Long tid, Long comment_id) {
        return reportRepository.findByCommentEntity_OngoingEntity_TidAndCommentEntity_Id(tid, comment_id);
    }

    @Override
    public void delete(ReportEntity reportEntity) {
        reportRepository.delete(reportEntity);
    }

    @Override
    public void deleteAll() {
        reportRepository.deleteAll();
    }
}
