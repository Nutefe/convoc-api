package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EvenementEquipeDao {
    EvenementEquipes save(EvenementEquipes evenementEquipe);
    EvenementEquipes selectById(EvenementEquipePK id);
    List<EvenementEquipes> selectByEquipe(Evenements evenement);
    List<Equipes> selectByEvenement(Evenements evenement);
    List<Evenements> selectByEquipe(Equipes equipe);
    List<Evenements> selectByEquipe(List<Long> equipe);
    List<Evenements> selectByEquipe(Equipes equipe, Pageable pageable);
    List<Evenements> recherche(Equipes equipe, String search, Pageable pageable);
    Long countByEquipe(Equipes equipe);
    Long countRecherche(Equipes equipe, String search);
    List<Evenements> selectByOrganisation(List<Long> orgs);
    List<Long> selectByOrganisationIds(List<Long> orgs);
    List<Long> selectByEvenementIds(Evenements evenement);
    List<Evenements> selectByOrganisation(List<Long> orgs, Pageable pageable);
    List<Evenements> recherche(List<Long> orgs, String search, Pageable pageable);
    Long countByOrganisation(List<Long> orgs);
    Long countRecherche(List<Long> orgs, String search);
    void delete(EvenementEquipes evenementEquipe);
    void delete(List<EvenementEquipes> evenementEquipes);
}
