package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.FeedbackEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface FeedbackService {
    Optional<FeedbackEntity> save(FeedbackEntity feedbackEntity);

    List<FeedbackEntity> saveAll(List<FeedbackEntity> feedbackEntityList);

    Optional<FeedbackEntity> get(Long id);

    List<FeedbackEntity> getAll();

    void delete(FeedbackEntity feedbackEntity);

    void deleteAll();
}
