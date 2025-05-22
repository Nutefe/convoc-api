package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.AccountOrganisations;
import com.cyberethik.convocapi.persistance.entities.Convocations;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ConvocationDao {
    Optional<Convocations> findById(Long id);
    Convocations save(Convocations convocation);
    Convocations selectById(Long id);
    List<Convocations> findByDeletedFalseOrderByIdDesc();
    List<Convocations> findByDeletedTrueOrderByIdDesc();
    Convocations findTop1ByEvenementAndDeletedFalse(Evenements evenement);
    List<Convocations> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Convocations> recherche(String search, Pageable pageable);
    Long countConvocations();
    Long countRecherche(String search);
    List<Convocations> selectByEvenement(Evenements evenement, Pageable pageable);
    List<Convocations> recherche(Evenements evenement, String search, Pageable pageable);
    Long countConvocations(Evenements evenement);
    Long countRecherche(Evenements evenement, String search);
    List<Convocations> selectByEvenement(List<Long> events, Pageable pageable);
    List<Convocations> recherche(List<Long> events, String search, Pageable pageable);
    Long countConvocations(List<Long> events);
    Long countRecherche(List<Long> events, String search);
    void delete(Convocations convocation);
    Convocations selectBySlug(String slug);
    boolean existsBySlug(final String slug);
    Long countConvocations(Long org, Date startDate, Date endDate);
    List<Membres> selectMembreByEvenement(Evenements evenement);
    List<Membres> selectMembreByEvenement(Evenements evenement, Pageable pageable);
    List<Membres> rechercheMembre(Evenements evenement, String search, Pageable pageable);
    Long countConvocationsMembre(Evenements evenement);
    Long countRechercheMembre(Evenements evenement, String search);
    List<Evenements> selectConvocations(Long org, Date startDate, Date endDate);
    List<Membres> rechercheMembre(Evenements evenement,
                                  List<String> equipes,
                                  List<String> membres,
                                  Pageable pageable);
    Long countRechercheMembre(Evenements evenement, List<String> equipes,
                              List<String> membres);
}
