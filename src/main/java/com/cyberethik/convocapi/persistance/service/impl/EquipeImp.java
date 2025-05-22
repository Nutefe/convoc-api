package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Membres;
import com.cyberethik.convocapi.persistance.repository.EquipeRepository;
import com.cyberethik.convocapi.persistance.service.dao.EquipeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EquipeImp implements EquipeDao {

    @Autowired
    private EquipeRepository repository;

    @Override
    public Optional<Equipes> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Equipes save(Equipes equipe) {
        return repository.save(equipe);
    }

    @Override
    public Equipes selectById(Long id) {
        return repository.selectById(id);
    }

    @Override
    public List<Equipes> findByDeletedFalseOrderByIdDesc() {
        return repository.findByDeletedFalseOrderByIdDesc();
    }

    @Override
    public List<Equipes> findByDeletedTrueOrderByIdDesc() {
        return repository.findByDeletedTrueOrderByIdDesc();
    }

    @Override
    public List<Equipes> findByDeletedFalseOrderByIdDesc(Pageable pageable) {
        return repository.findByDeletedFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<Equipes> recherche(String search, Pageable pageable) {
        return repository.recherche(search, pageable);
    }

    @Override
    public Long countEquipes() {
        return repository.countEquipes();
    }

    @Override
    public Long countRecherche(String search) {
        return repository.countRecherche(search);
    }

    @Override
    public List<Equipes> seletByOrganisation(List<Long> orgs) {
        return repository.seletByOrganisation(orgs);
    }

    @Override
    public List<Equipes> seletByOrganisation(List<Long> orgs, Pageable pageable) {
        return repository.seletByOrganisation(orgs, pageable);
    }

    @Override
    public List<Equipes> recherche(List<Long> orgs, String search, Pageable pageable) {
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
    public void delete(Equipes equipe) {
        repository.delete(equipe);
    }

    @Override
    public Long countByOrganisation(Long orgs) {
        return repository.countByOrganisation(orgs);
    }

    @Override
    public List<Equipes> recherche(List<Long> orgs, List<String> noms, Pageable pageable) {
        return repository.recherche(orgs, noms, pageable);
    }

    @Override
    public Long countRecherche(List<Long> orgs, List<String> noms) {
        return repository.countRecherche(orgs, noms);
    }

    @Override
    public List<Equipes> rechercheActif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable) {
        return repository.rechercheActif(orgs, noms, endDate, pageable);
    }

    @Override
    public Long countRechercheActif(List<Long> orgs, List<String> noms, Date endDate) {
        return repository.countRechercheActif(orgs, noms, endDate);
    }

    @Override
    public List<Equipes> rechercheInactif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable) {
        return repository.rechercheInactif(orgs, noms, endDate, pageable);
    }

    @Override
    public Long countRechercheInactif(List<Long> orgs, List<String> noms, Date endDate) {
        return repository.countRechercheInactif(orgs, noms, endDate);
    }

    @Override
    public List<Equipes> recherche(List<Long> orgs, Pageable pageable) {
        return repository.recherche(orgs, pageable);
    }

    @Override
    public List<Equipes> selectOrganisation(List<Long> orgs, Membres membre, Date endDate) {
        return repository.selectOrganisation(orgs, membre, endDate);
    }

    @Override
    public Long countRecherche(List<Long> orgs) {
        return repository.countRecherche(orgs);
    }
}
