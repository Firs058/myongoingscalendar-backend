package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.GenreEntity;
import org.myongoingscalendar.repository.GenreRepository;
import org.myongoingscalendar.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Optional<GenreEntity> save(GenreEntity genreEntity) {
        return Optional.of(genreRepository.save(genreEntity));
    }

    @Override
    public List<GenreEntity> saveAll(List<GenreEntity> genreEntityList) {
        return genreRepository.saveAll(genreEntityList);
    }

    @Override
    public Optional<GenreEntity> get(Long id) {
        return Optional.of(genreRepository.getOne(id));
    }

    @Override
    public Optional<GenreEntity> findByName(String name) {
        return genreRepository.findByName(name);
    }

    @Override
    @Cacheable("getAllGenres")
    public List<GenreEntity> getAll() {
        return genreRepository.findAll();
    }

    @Override
    public void delete(GenreEntity genreEntity) {
        genreRepository.delete(genreEntity);
    }

    @Override
    public void deleteAll() {
        genreRepository.deleteAll();
    }
}
