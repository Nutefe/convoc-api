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
    public Responsables selectByEmail(String email) {
        return repository.selectByEmail(email);
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

    @Override
    public List<Responsables> seletByOrganisation(List<Long> orgs) {
        return repository.seletByOrganisation(orgs);
    }

    @Override
    public List<Responsables> seletByOrganisation(List<Long> orgs, Pageable pageable) {
        return repository.seletByOrganisation(orgs, pageable);
    }

    @Override
    public List<Responsables> recherche(List<Long> orgs, String search, Pageable pageable) {
        return repository.recherche(orgs, search, pageable);
    }

    @Override
    public Long countOrganisation(List<Long> orgs) {
        return repository.countOrganisation(orgs);
    }

    @Override
    public Long countRecherche(List<Long> orgs, String search) {
        return repository.countRecherche(orgs, search);
    }

    @Override
    public boolean existsByEmail(String email, Long id) {
        return repository.existsByEmail(email, id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
