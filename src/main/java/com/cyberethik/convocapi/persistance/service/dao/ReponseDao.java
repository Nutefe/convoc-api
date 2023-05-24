package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.EvenementEquipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReponseDao {
    Optional<Reponses> findById(Long id);
    Reponses save(Reponses reponse);
    Reponses selectById(Long id);
    List<Reponses> findByDeletedFalseOrderByIdDesc();
    List<Reponses> findByDeletedTrueOrderByIdDesc();
    List<Reponses> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Reponses> recherche(String search, Pageable pageable);
    Long countReponses();
    Long countRecherche(String search);
    List<Reponses> selectByEvenement(Evenements evenement, Pageable pageable);
    List<Reponses> recherche(Evenements evenement, String search, Pageable pageable);
    Long countReponses(Evenements evenement);
    Long countRecherche(Evenements evenement, String search);
    List<Reponses> selectByEvenement(List<Long> events, Pageable pageable);
    List<Reponses> recherche(List<Long> events, String search, Pageable pageable);
    Long countReponses(List<Long> events);
    Long countRecherche(List<Long> events, String search);
    void delete(Reponses reponse);
}
