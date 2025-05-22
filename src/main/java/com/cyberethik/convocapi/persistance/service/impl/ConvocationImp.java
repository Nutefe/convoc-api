package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Convocations;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
import com.cyberethik.convocapi.persistance.repository.ConvocationRepository;
import com.cyberethik.convocapi.persistance.service.dao.ConvocationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ConvocationImp implements ConvocationDao {

    @Autowired
    private ConvocationRepository repository;

    @Override
    public Optional<Convocations> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Convocations save(Convocations convocation) {
        return repository.save(convocation);
    }

    @Override
    public Convocations selectById(Long id) {
        return repository.selectById(id);
    }

    @Override
    public List<Convocations> findByDeletedFalseOrderByIdDesc() {
        return repository.findByDeletedFalseOrderByIdDesc();
    }

    @Override
    public List<Convocations> findByDeletedTrueOrderByIdDesc() {
        return repository.findByDeletedTrueOrderByIdDesc();
    }

    @Override
    public Convocations findTop1ByEvenementAndDeletedFalse(Evenements evenement) {
        return repository.findTop1ByEvenementAndDeletedFalse(evenement);
    }

    @Override
    public List<Convocations> findByDeletedFalseOrderByIdDesc(Pageable pageable) {
        return repository.findByDeletedFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<Convocations> recherche(String search, Pageable pageable) {
        return repository.recherche(search, pageable);
    }

    @Override
    public Long countConvocations() {
        return repository.countConvocations();
    }

    @Override
    public Long countRecherche(String search) {
        return repository.countRecherche(search);
    }

    @Override
    public List<Convocations> selectByEvenement(Evenements evenement, Pageable pageable) {
        return repository.selectByEvenement(evenement, pageable);
    }

    @Override
    public List<Convocations> recherche(Evenements evenement, String search, Pageable pageable) {
        return repository.recherche(evenement, search, pageable);
    }

    @Override
    public Long countConvocations(Evenements evenement) {
        return repository.countConvocations(evenement);
    }

    @Override
    public Long countRecherche(Evenements evenement, String search) {
        return repository.countRecherche(evenement, search);
    }

    @Override
    public List<Convocations> selectByEvenement(List<Long> events, Pageable pageable) {
        return repository.selectByEvenement(events, pageable);
    }

    @Override
    public List<Convocations> recherche(List<Long> events, String search, Pageable pageable) {
        return repository.recherche(events, search, pageable);
    }

    @Override
    public Long countConvocations(List<Long> events) {
        return repository.countConvocations(events);
    }

    @Override
    public Long countRecherche(List<Long> events, String search) {
        return repository.countRecherche(events, search);
    }

    @Override
    public void delete(Convocations convocation) {
        repository.delete(convocation);
    }

    @Override
    public Convocations selectBySlug(String slug) {
        return repository.selectBySlug(slug);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public Long countConvocations(Long org, Date startDate, Date endDate) {
        return repository.countConvocations(org, startDate, endDate);
    }

    @Override
    public List<Membres> selectMembreByEvenement(Evenements evenement) {
        return repository.selectMembreByEvenement(evenement);
    }

    @Override
    public List<Membres> selectMembreByEvenement(Evenements evenement, Pageable pageable) {
        return repository.selectMembreByEvenement(evenement, pageable);
    }

    @Override
    public List<Membres> rechercheMembre(Evenements evenement, String search, Pageable pageable) {
        return repository.rechercheMembre(evenement, search, pageable);
    }

    @Override
    public Long countConvocationsMembre(Evenements evenement) {
        return repository.countConvocationsMembre(evenement);
    }

    @Override
    public Long countRechercheMembre(Evenements evenement, String search) {
        return repository.countRechercheMembre(evenement, search);
    }

    @Override
    public List<Evenements> selectConvocations(Long org, Date startDate, Date endDate) {
        return repository.selectConvocations(org, startDate, endDate);
    }

    @Override
    public List<Membres> rechercheMembre(Evenements evenement, List<String> equipes, List<String> membres, Pageable pageable) {
        return repository.rechercheMembre(evenement, equipes, membres, pageable);
    }

    @Override
    public Long countRechercheMembre(Evenements evenement, List<String> equipes, List<String> membres) {
        return repository.countRechercheMembre(evenement, equipes, membres);
    }
}
