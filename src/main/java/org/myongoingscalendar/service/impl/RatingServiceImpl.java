package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.OngoingEntity;
import org.myongoingscalendar.entity.RatingEntity;
import org.myongoingscalendar.repository.RatingRepository;
import org.myongoingscalendar.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }


    @Override
    public Optional<RatingEntity> save(RatingEntity ratingEntity) {
        return Optional.of(ratingRepository.save(ratingEntity));
    }

    @Override
    public List<RatingEntity> saveAll(List<RatingEntity> ratingEntityList) {
        return ratingRepository.saveAll(ratingEntityList);
    }

    @Override
    public Optional<RatingEntity> get(Long id) {
        return Optional.of(ratingRepository.getOne(id));
    }

    @Override
    public List<RatingEntity> getAll() {
        return ratingRepository.findAll();
    }

    @Override
    public List<RatingEntity> getAllByOngoingEntity(OngoingEntity ongoingEntity) {
        return ratingRepository.getAllByOngoingEntity(ongoingEntity);
    }

    @Override
    public void delete(RatingEntity ratingEntity) {
        ratingRepository.delete(ratingEntity);
    }

    @Override
    public void deleteAll() {
        ratingRepository.deleteAll();
    }
}
