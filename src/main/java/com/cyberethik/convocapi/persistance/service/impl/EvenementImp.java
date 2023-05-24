package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.repository.EvenementRepository;
import com.cyberethik.convocapi.persistance.service.dao.EvenementDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvenementImp implements EvenementDao {

    @Autowired
    private EvenementRepository repository;

    @Override
    public Optional<Evenements> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Evenements save(Evenements evenement) {
        return repository.save(evenement);
    }

    @Override
    public Evenements selectById(Long id) {
        return repository.selectById(id);
    }

    @Override
    public List<Evenements> findByDeletedFalseOrderByIdDesc() {
        return repository.findByDeletedFalseOrderByIdDesc();
    }

    @Override
    public List<Evenements> findByDeletedTrueOrderByIdDesc() {
        return repository.findByDeletedTrueOrderByIdDesc();
    }

    @Override
    public List<Evenements> findByDeletedFalseOrderByIdDesc(Pageable pageable) {
        return repository.findByDeletedFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<Evenements> recherche(String search, Pageable pageable) {
        return repository.recherche(search, pageable);
    }

    @Override
    public Long countEvenements() {
        return repository.countEvenements();
    }

    @Override
    public Long countRecherche(String search) {
        return repository.countRecherche(search);
    }

    @Override
    public void delete(Evenements evenement) {
        repository.delete(evenement);
    }
}
