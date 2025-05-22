package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
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
    Long countReponse(Long org, Date endDate);
    Long countReponsePositif(Evenements evenement);
    Long countReponseNegatif(Evenements evenement);
    List<Membres> recherche(Evenements evenement, List<String> equipes,
                            List<String> membres,
                            List<Date> dateReponse,
                            List<String> reponses,
                            Pageable pageable);
    Long countRecherche(Evenements evenement, List<String> equipes,
                        List<String> membres,
                        List<Date> dateReponse,
                        List<String> reponses);
    List<Reponses> selectByEvenement(Evenements evenement);
    List<Reponses> membreEvenement(Evenements evenement, Membres membre);
    List<Membres> recherche(Evenements evenement, List<String> equipes,
                            List<String> membres,
                            Date dateReponse1,
                            Date dateReponse2,
                            List<String> reponses,
                            Pageable pageable);
    Long countRecherche(Evenements evenement,
                        List<String> equipes,
                        List<String> membres,
                        Date dateReponse1,
                        Date dateReponse2,
                        List<String> reponses);
    List<Membres> recherche(Evenements evenement,
                            List<String> equipes,
                            List<String> membres,
                            List<String> reponses,
                            Pageable pageable);
    Long countRecherche(Evenements evenement,
                        List<String> equipes,
                        List<String> membres,
                        List<String> reponses);
    Reponses findTop1(Convocations convocation);
    void delete(Reponses reponse);
}
