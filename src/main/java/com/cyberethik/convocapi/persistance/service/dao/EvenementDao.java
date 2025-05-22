package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EvenementDao {
    Optional<Evenements> findById(Long id);
    Evenements save(Evenements evenement);
    Evenements selectById(Long id);
    List<Evenements> findByDeletedFalseOrderByIdDesc();
    List<Evenements> findByDeletedTrueOrderByIdDesc();
    List<Evenements> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Evenements> recherche(String search, Pageable pageable);
    Long countEvenements();
    Long countRecherche(String search);
    Long countEvenements(Long org, Date end);
    void delete(Evenements evenement);

    List<Evenements> recherche(List<Long> orgs, List<String> noms, Pageable pageable);
    Long countRecherche(List<Long> orgs, List<String> noms);
    List<Evenements> recherche(List<Long> orgs, Pageable pageable);
    Long countRecherche(List<Long> orgs);
    List<Evenements> rechercheActif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable);
    Long countRechercheActif(List<Long> orgs, List<String> noms, Date endDate);
    List<Evenements> rechercheInactif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable);
    Long countRechercheInactif(List<Long> orgs, List<String> noms, Date endDate);

    List<Evenements> selectByOrganisation(List<Long> orgs);
    List<Evenements> selectByOrganisation(List<Long> orgs, Pageable pageable);
    List<Evenements> recherche(List<Long> orgs, String search, Pageable pageable);
    Long countByOrganisation(List<Long> orgs);
    Long countRecherche(List<Long> orgs, String search);
}
