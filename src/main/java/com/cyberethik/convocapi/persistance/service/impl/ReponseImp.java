package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import com.cyberethik.convocapi.persistance.repository.ReponseRepository;
import com.cyberethik.convocapi.persistance.service.dao.ReponseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReponseImp implements ReponseDao {

    @Autowired
    private ReponseRepository repository;

    @Override
    public Optional<Reponses> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Reponses save(Reponses reponse) {
        return repository.save(reponse);
    }

    @Override
    public Reponses selectById(Long id) {
        return repository.selectById(id);
    }

    @Override
    public List<Reponses> findByDeletedFalseOrderByIdDesc() {
        return repository.findByDeletedFalseOrderByIdDesc();
    }

    @Override
    public List<Reponses> findByDeletedTrueOrderByIdDesc() {
        return repository.findByDeletedTrueOrderByIdDesc();
    }

    @Override
    public List<Reponses> findByDeletedFalseOrderByIdDesc(Pageable pageable) {
        return repository.findByDeletedFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<Reponses> recherche(String search, Pageable pageable) {
        return repository.recherche(search, pageable);
    }

    @Override
    public Long countReponses() {
        return repository.countReponses();
    }

    @Override
    public Long countRecherche(String search) {
        return repository.countRecherche(search);
    }

    @Override
    public List<Reponses> selectByEvenement(Evenements evenement, Pageable pageable) {
        return repository.selectByEvenement(evenement, pageable);
    }

    @Override
    public List<Reponses> recherche(Evenements evenement, String search, Pageable pageable) {
        return repository.recherche(evenement, search, pageable);
    }

    @Override
    public Long countReponses(Evenements evenement) {
        return repository.countReponses(evenement);
    }

    @Override
    public Long countRecherche(Evenements evenement, String search) {
        return repository.countRecherche(evenement, search);
    }

    @Override
    public List<Reponses> selectByEvenement(List<Long> events, Pageable pageable) {
        return repository.selectByEvenement(events, pageable);
    }

    @Override
    public List<Reponses> recherche(List<Long> events, String search, Pageable pageable) {
        return repository.recherche(events, search, pageable);
    }

    @Override
    public Long countReponses(List<Long> events) {
        return repository.countReponses(events);
    }

    @Override
    public Long countRecherche(List<Long> events, String search) {
        return repository.countRecherche(events, search);
    }

    @Override
    public void delete(Reponses reponse) {
        repository.delete(reponse);
    }
}
