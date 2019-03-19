package org.myongoingscalendar.service;

import org.myongoingscalendar.entity.RatingEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
public interface RatingService {
    Optional<RatingEntity> save(RatingEntity ratingEntity);

    List<RatingEntity> saveAll(List<RatingEntity> ratingEntityList);

    Optional<RatingEntity> get(Long id);

    List<RatingEntity> getAll();

    void delete(RatingEntity ratingEntity);

    void deleteAll();
}
