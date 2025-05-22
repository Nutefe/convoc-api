package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Membres;
import com.cyberethik.convocapi.persistance.repository.MembresRepository;
import com.cyberethik.convocapi.persistance.service.dao.MembreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public List<Membres> selectByOrganisation(List<Long> orgs) {
        return repository.selectByOrganisation(orgs);
    }

    @Override
    public List<Membres> selectByOrganisation(List<Long> orgs, Pageable pageable) {
        return repository.selectByOrganisation(orgs, pageable);
    }

    @Override
    public List<Membres> recherche(List<Long> orgs, String search, Pageable pageable) {
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

    @Override
    public List<Membres> recherche(List<Long> orgs, List<String> noms, Pageable pageable) {
        return repository.recherche(orgs, noms, pageable);
    }

    @Override
    public Long countRecherche(List<Long> orgs, List<String> noms) {
        return repository.countRecherche(orgs, noms);
    }

    @Override
    public List<Membres> recherche(List<Long> orgs, Pageable pageable) {
        return repository.recherche(orgs, pageable);
    }

    @Override
    public Long countRecherche(List<Long> orgs) {
        return repository.countRecherche(orgs);
    }

    @Override
    public List<Membres> rechercheActif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable) {
        return repository.rechercheActif(orgs, noms, endDate, pageable);
    }

    @Override
    public Long countRechercheActif(List<Long> orgs, List<String> noms, Date endDate) {
        return repository.countRechercheActif(orgs, noms, endDate);
    }

    @Override
    public List<Membres> rechercheInactif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable) {
        return repository.rechercheInactif(orgs, noms, endDate, pageable);
    }

    @Override
    public Long countRechercheInactif(List<Long> orgs, List<String> noms, Date endDate) {
        return repository.countRechercheInactif(orgs, noms, endDate);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public boolean existsByEmail(String email, Long id) {
        return repository.existsByEmail(email, id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public List<Membres> selectByOrganisation(List<Long> orgs, Equipes equipe, Date endDate) {
        return repository.selectByOrganisation(orgs, equipe, endDate);
    }

    @Override
    public List<Membres> selectByOrganisation(List<Long> orgs, Date endDate) {
        return repository.selectByOrganisation(orgs, endDate);
    }

    @Override
    public void delete(Membres membre) {
        repository.delete(membre);
    }
}
