package org.myongoingscalendar.service.impl;

import org.myongoingscalendar.entity.MalTitleGenreEntity;
import org.myongoingscalendar.repository.MalTitleGenreRepository;
import org.myongoingscalendar.service.MalTitleGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author firs
 */
@Service
public class MalTitleGenreServiceImpl implements MalTitleGenreService {

    private final MalTitleGenreRepository malTitleGenreRepository;

    @Autowired
    public MalTitleGenreServiceImpl(MalTitleGenreRepository malTitleGenreRepository) {
        this.malTitleGenreRepository = malTitleGenreRepository;
    }

    @Override
    public Optional<MalTitleGenreEntity> save(MalTitleGenreEntity malTitleGenreEntity) {
        return Optional.of(malTitleGenreRepository.save(malTitleGenreEntity));
    }

    @Override
    public List<MalTitleGenreEntity> saveAll(List<MalTitleGenreEntity> malTitleGenreEntityList) {
        return malTitleGenreRepository.saveAll(malTitleGenreEntityList);
    }

    @Override
    public Optional<MalTitleGenreEntity> get(Long id) {
        return Optional.of(malTitleGenreRepository.getOne(id));
    }

    @Override
    public List<MalTitleGenreEntity> getAll() {
        return malTitleGenreRepository.findAll();
    }

    @Override
    public void delete(MalTitleGenreEntity malTitleGenreEntity) {
        malTitleGenreRepository.delete(malTitleGenreEntity);
    }

    @Override
    public void deleteAll() {
        malTitleGenreRepository.deleteAll();
    }
}
