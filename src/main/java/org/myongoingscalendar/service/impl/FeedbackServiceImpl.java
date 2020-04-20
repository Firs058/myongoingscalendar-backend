package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.FeedbackEntity;
import org.myongoingscalendar.repository.FeedbackRepository;
import org.myongoingscalendar.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public Optional<FeedbackEntity> save(FeedbackEntity feedbackEntity) {
        return Optional.of(feedbackRepository.save(feedbackEntity));
    }

    @Override
    public List<FeedbackEntity> saveAll(List<FeedbackEntity> feedbackEntityList) {
        return feedbackRepository.saveAll(feedbackEntityList);
    }

    @Override
    public Optional<FeedbackEntity> get(Long id) {
        return Optional.of(feedbackRepository.getOne(id));
    }

    @Override
    public List<FeedbackEntity> getAll() {
        return feedbackRepository.findAll();
    }

    @Override
    public void delete(FeedbackEntity feedbackEntity) {
        feedbackRepository.delete(feedbackEntity);
    }

    @Override
    public void deleteAll() {
        feedbackRepository.deleteAll();
    }
}
