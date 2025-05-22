package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface EquipeMembreDao {
    EquipeMembres save(EquipeMembres equipeMembre);
    EquipeMembres selectById(EquipeMembrePK id);
    List<EquipeMembres> selectByEquipe(Membres membre);
    List<Membres> selectByEquipe(Equipes equipe);
    List<Equipes> selectByMembre(Membres membre);
    List<Long> selectByMembreIds(Membres membre);
    List<Membres> selectByEquipe(Equipes equipe, Pageable pageable);
    List<Membres> recherche(Equipes equipe, String search, Pageable pageable);
    Long countByEquipe(Equipes equipe);
    Long countRecherche(Equipes equipe, String search);
    List<Membres> selectByOrganisation(List<Long> orgs);
    List<Membres> selectByOrganisation(List<Long> orgs, Pageable pageable);
    List<Membres> recherche(List<Long> orgs, String search, Pageable pageable);
    Long countByOrganisation(List<Long> orgs);
    Long countRecherche(List<Long> orgs, String search);
    List<Responsables> selectByOrganisationRes(List<Long> orgs);
    List<Responsables> selectByOrganisationRes(List<Long> orgs, Pageable pageable);
    List<Responsables> rechercheRes(List<Long> orgs, String search, Pageable pageable);
    Long countByOrganisationRes(List<Long> orgs);
    Long countRechercheRes(List<Long> orgs, String search);
    void delete(EquipeMembres equipeMembre);
    void delete(List<EquipeMembres> equipeMembres);
    Long countByOrganisation(Long orgs);
    List<Membres> selectByEquipes(List<Long> eqp);
    Long countByEquipe(Equipes equipe, Date end);
    List<Membres> membreByEquipe(Equipes equipe, Date end);
}
