package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Convocations;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import com.cyberethik.convocapi.persistance.repository.ReponseRepository;
import com.cyberethik.convocapi.persistance.service.dao.ReponseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public Long countReponse(Long org, Date endDate) {
        return repository.countReponse(org, endDate);
    }

    @Override
    public Long countReponsePositif(Evenements evenement) {
        return repository.countReponsePositif(evenement);
    }

    @Override
    public Long countReponseNegatif(Evenements evenement) {
        return repository.countReponseNegatif(evenement);
    }

    @Override
    public List<Membres> recherche(Evenements evenement, List<String> equipes, List<String> membres, List<Date> dateReponse, List<String> reponses, Pageable pageable) {
        return repository.recherche(evenement, equipes, membres, dateReponse, reponses, pageable);
    }

    @Override
    public Long countRecherche(Evenements evenement, List<String> equipes, List<String> membres, List<Date> dateReponse, List<String> reponses) {
        return repository.countRecherche(evenement, equipes, membres, dateReponse, reponses);
    }

    @Override
    public List<Reponses> selectByEvenement(Evenements evenement) {
        return repository.selectByEvenement(evenement);
    }

    @Override
    public List<Reponses> membreEvenement(Evenements evenement, Membres membre) {
        return repository.membreEvenement(evenement, membre);
    }

    @Override
    public List<Membres> recherche(Evenements evenement, List<String> equipes, List<String> membres, Date dateReponse1, Date dateReponse2, List<String> reponses, Pageable pageable) {
        return repository.recherche(evenement, equipes, membres, dateReponse1, dateReponse2, reponses, pageable);
    }

    @Override
    public Long countRecherche(Evenements evenement, List<String> equipes, List<String> membres, Date dateReponse1, Date dateReponse2, List<String> reponses) {
        return repository.countRecherche(evenement, equipes, membres, dateReponse1, dateReponse2, reponses);
    }

    @Override
    public List<Membres> recherche(Evenements evenement, List<String> equipes, List<String> membres, List<String> reponses, Pageable pageable) {
        return repository.recherche(evenement, equipes, membres, reponses, pageable);
    }

    @Override
    public Long countRecherche(Evenements evenement, List<String> equipes, List<String> membres, List<String> reponses) {
        return repository.countRecherche(evenement, equipes, membres, reponses);
    }

    @Override
    public Reponses findTop1(Convocations convocation) {
        return repository.findTop1ByConvocationAndDeletedFalseOrderByIdDesc(convocation);
    }

    @Override
    public void delete(Reponses reponse) {
        repository.delete(reponse);
    }
}
