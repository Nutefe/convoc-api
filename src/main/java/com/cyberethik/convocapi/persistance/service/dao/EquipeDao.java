package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.AccountOrganisations;
import com.cyberethik.convocapi.persistance.entities.Convocations;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EquipeDao {
    Optional<Equipes> findById(Long id);
    Equipes save(Equipes equipe);
    Equipes selectById(Long id);
    List<Equipes> findByDeletedFalseOrderByIdDesc();
    List<Equipes> findByDeletedTrueOrderByIdDesc();
    List<Equipes> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Equipes> recherche(String search, Pageable pageable);
    Long countEquipes();
    Long countRecherche(String search);
    List<Equipes> seletByOrganisation(List<Long> orgs);
    List<Equipes> seletByOrganisation(List<Long> orgs, Pageable pageable);
    List<Equipes> recherche(List<Long> orgs, String search, Pageable pageable);
    Long countOrganisation(List<Long> orgs);
    Long countRecherche(List<Long> orgs, String search);
    void delete(Equipes equipe);
    Long countByOrganisation(Long orgs);
}
