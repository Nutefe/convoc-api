package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.repository.EvenementRepository;
import com.cyberethik.convocapi.persistance.service.dao.EvenementDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public Long countEvenements(Long org, Date end) {
        return repository.countEvenements(org, end);
    }

    @Override
    public void delete(Evenements evenement) {
        repository.delete(evenement);
    }

    @Override
    public List<Evenements> recherche(List<Long> orgs, List<String> noms, Pageable pageable) {
        return repository.recherche(orgs, noms, pageable);
    }

    @Override
    public Long countRecherche(List<Long> orgs, List<String> noms) {
        return repository.countRecherche(orgs, noms);
    }

    @Override
    public List<Evenements> recherche(List<Long> orgs, Pageable pageable) {
        return repository.recherche(orgs, pageable);
    }

    @Override
    public Long countRecherche(List<Long> orgs) {
        return repository.countRecherche(orgs);
    }

    @Override
    public List<Evenements> rechercheActif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable) {
        return repository.rechercheActif(orgs, noms, endDate, pageable);
    }

    @Override
    public Long countRechercheActif(List<Long> orgs, List<String> noms, Date endDate) {
        return repository.countRechercheActif(orgs, noms, endDate);
    }

    @Override
    public List<Evenements> rechercheInactif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable) {
        return repository.rechercheInactif(orgs, noms, endDate, pageable);
    }

    @Override
    public Long countRechercheInactif(List<Long> orgs, List<String> noms, Date endDate) {
        return repository.countRechercheInactif(orgs, noms, endDate);
    }

    @Override
    public List<Evenements> selectByOrganisation(List<Long> orgs) {
        return repository.selectByOrganisation(orgs);
    }

    @Override
    public List<Evenements> selectByOrganisation(List<Long> orgs, Pageable pageable) {
        return repository.selectByOrganisation(orgs, pageable);
    }

    @Override
    public List<Evenements> recherche(List<Long> orgs, String search, Pageable pageable) {
        return repository.recherche(orgs, search, pageable);
    }

    @Override
    public Long countByOrganisation(List<Long> orgs) {
        return repository.countByOrganisation(orgs);
    }

    @Override
    public Long countRecherche(List<Long> orgs, String search) {
        return repository.countRecherche(orgs, search);
    }
}
