package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Responsables;
import com.cyberethik.convocapi.persistance.repository.ResponsableRepository;
import com.cyberethik.convocapi.persistance.service.dao.ResponsableDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResponsableImp implements ResponsableDao {

    @Autowired
    private ResponsableRepository repository;

    @Override
    public Optional<Responsables> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Responsables save(Responsables responsable) {
        return repository.save(responsable);
    }

    @Override
    public Responsables selectById(Long id) {
        return repository.selectById(id);
    }

    @Override
    public List<Responsables> findByDeletedFalseOrderByIdDesc() {
        return repository.findByDeletedFalseOrderByIdDesc();
    }

    @Override
    public List<Responsables> findByDeletedTrueOrderByIdDesc() {
        return repository.findByDeletedTrueOrderByIdDesc();
    }

    @Override
    public List<Responsables> findByDeletedFalseOrderByIdDesc(Pageable pageable) {
        return repository.findByDeletedFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<Responsables> recherche(String search, Pageable pageable) {
        return repository.recherche(search, pageable);
    }

    @Override
    public Long countResponsables() {
        return repository.countResponsables();
    }

    @Override
    public Long countRecherche(String search) {
        return repository.countRecherche(search);
    }

    @Override
    public void delete(Responsables responsable) {
        repository.delete(responsable);
    }
}
