package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.EvenementEquipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MembreDao {
    Optional<Membres> findById(Long id);
    Membres save(Membres membre);
    Membres selectById(Long id);
    Membres selectBySlug(String slug);
    List<Membres> findByDeletedFalseOrderByIdDesc();
    List<Membres> findByDeletedTrueOrderByIdDesc();
    List<Membres> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Membres> recherche(String search, Pageable pageable);
    Long countMembres();
    Long countRecherche(String search);


    List<Membres> selectByOrganisation(List<Long> orgs);
    List<Membres> selectByOrganisation(List<Long> orgs, Pageable pageable);
    List<Membres> recherche(List<Long> orgs, String search, Pageable pageable);
    Long countByOrganisation(List<Long> orgs);
    Long countRecherche(List<Long> orgs, String search);

    List<Membres> recherche(List<Long> orgs, List<String> noms, Pageable pageable);
    Long countRecherche(List<Long> orgs, List<String> noms);
    List<Membres> recherche(List<Long> orgs, Pageable pageable);
    Long countRecherche(List<Long> orgs);
    List<Membres> rechercheActif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable);
    Long countRechercheActif(List<Long> orgs, List<String> noms, Date endDate);
    List<Membres> rechercheInactif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable);
    Long countRechercheInactif(List<Long> orgs, List<String> noms, Date endDate);

    boolean existsBySlug(final String slug);
    boolean existsByEmail(final String email, final Long id);
    boolean existsByEmail(final String email);
    List<Membres> selectByOrganisation(List<Long> orgs, Equipes equipe, Date endDate);
    List<Membres> selectByOrganisation(List<Long> orgs, Date endDate);
    void delete(Membres membre);
}
