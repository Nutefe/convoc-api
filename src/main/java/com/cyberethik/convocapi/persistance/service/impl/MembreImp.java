package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Membres;
import com.cyberethik.convocapi.persistance.repository.MembresRepository;
import com.cyberethik.convocapi.persistance.service.dao.MembreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembreImp implements MembreDao
{
    @Autowired
    private MembresRepository repository;

    @Override
    public Optional<Membres> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Membres save(Membres membre) {
        return repository.save(membre);
    }

    @Override
    public Membres selectById(Long id) {
        return repository.selectById(id);
    }

    @Override
    public Membres selectBySlug(String slug) {
        return repository.selectBySlug(slug);
    }

    @Override
    public List<Membres> findByDeletedFalseOrderByIdDesc() {
        return repository.findByDeletedFalseOrderByIdDesc();
    }

    @Override
    public List<Membres> findByDeletedTrueOrderByIdDesc() {
        return repository.findByDeletedTrueOrderByIdDesc();
    }

    @Override
    public List<Membres> findByDeletedFalseOrderByIdDesc(Pageable pageable) {
        return repository.findByDeletedFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<Membres> recherche(String search, Pageable pageable) {
        return repository.recherche(search, pageable);
    }

    @Override
    public Long countMembres() {
        return repository.countMembres();
    }

    @Override
    public Long countRecherche(String search) {
        return repository.countRecherche(search);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public void delete(Membres membre) {
        repository.delete(membre);
    }
}
