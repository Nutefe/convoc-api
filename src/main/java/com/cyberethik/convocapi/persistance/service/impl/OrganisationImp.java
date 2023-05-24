package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Organisations;
import com.cyberethik.convocapi.persistance.repository.OrganisationRepository;
import com.cyberethik.convocapi.persistance.service.dao.OrganisationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganisationImp implements OrganisationDao {

    @Autowired
    private OrganisationRepository repository;

    @Override
    public Optional<Organisations> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Organisations save(Organisations organisation) {
        return repository.save(organisation);
    }

    @Override
    public Organisations selectById(Long id) {
        return repository.selectById(id);
    }

    @Override
    public List<Organisations> findByDeletedFalseOrderByIdDesc() {
        return repository.findByDeletedFalseOrderByIdDesc();
    }

    @Override
    public List<Organisations> findByDeletedTrueOrderByIdDesc() {
        return repository.findByDeletedTrueOrderByIdDesc();
    }

    @Override
    public List<Organisations> findByDeletedFalseOrderByIdDesc(Pageable pageable) {
        return repository.findByDeletedFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<Organisations> recherche(String search, Pageable pageable) {
        return repository.recherche(search, pageable);
    }

    @Override
    public Long countOrganisations() {
        return repository.countOrganisations();
    }

    @Override
    public Long countRecherche(String search) {
        return repository.countRecherche(search);
    }

    @Override
    public void delete(Organisations organisation) {
        repository.delete(organisation);
    }
}
