package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EvenementEquipeRepository extends JpaRepository<EvenementEquipes, EvenementEquipePK> {
    @Query("SELECT x FROM EvenementEquipes x WHERE x.deleted = false AND x.id=:id")
    EvenementEquipes selectById(EvenementEquipePK id);
    @Query("SELECT x FROM EvenementEquipes x WHERE x.deleted = false AND x.id.evenement=:evenement")
    List<EvenementEquipes> selectByEquipe(Evenements evenement);
    @Query("SELECT x.id.equipe FROM EvenementEquipes x WHERE x.deleted = false AND x.id.evenement=:evenement")
    List<Equipes> selectByEvenement(Evenements evenement);
    @Query("SELECT DISTINCT x.id.evenement FROM EvenementEquipes x WHERE x.deleted = false AND x.id.equipe=:equipe")
    List<Evenements> selectByEquipe(Equipes equipe);
    @Query("SELECT DISTINCT x.id.evenement FROM EvenementEquipes x WHERE x.deleted = false AND x.id.equipe.id IN :equipe")
    List<Evenements> selectByEquipe(List<Long> equipe);
    @Query("SELECT DISTINCT x.id.evenement FROM EvenementEquipes x WHERE x.deleted = false AND x.id.equipe=:equipe")
    List<Evenements> selectByEquipe(Equipes equipe, Pageable pageable);
    @Query("SELECT DISTINCT x.id.evenement FROM EvenementEquipes x WHERE " +
            "(x.id.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.dateDebut LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.dateFin LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.equipe=:equipe)")
    List<Evenements> recherche(Equipes equipe, String search, Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x.id.evenement) FROM EvenementEquipes x WHERE x.deleted = false AND x.id.equipe=:equipe")
    Long countByEquipe(Equipes equipe);
    @Query("SELECT COUNT(DISTINCT x.id.evenement) FROM EvenementEquipes x WHERE " +
            "(x.id.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.dateDebut LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.dateFin LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.equipe=:equipe)")
    Long countRecherche(Equipes equipe, String search);

    @Query("SELECT x.id.evenement FROM EvenementEquipes x WHERE x.deleted = false AND x.id.equipe.organisation.id IN :orgs")
    List<Evenements> selectByOrganisation(List<Long> orgs);
    @Query("SELECT DISTINCT x.id.evenement.id FROM EvenementEquipes x WHERE x.deleted = false AND x.id.equipe.organisation.id IN :orgs")
    List<Long> selectByOrganisationIds(List<Long> orgs);
    @Query("SELECT DISTINCT x.id.equipe.id FROM EvenementEquipes x WHERE x.deleted = false AND x.id.evenement = :evenement")
    List<Long> selectByEvenementIds(Evenements evenement);
    @Query("SELECT DISTINCT x.id.evenement FROM EvenementEquipes x WHERE x.deleted = false AND x.id.equipe.organisation.id IN :orgs")
    List<Evenements> selectByOrganisation(List<Long> orgs, Pageable pageable);
    @Query("SELECT DISTINCT x.id.evenement FROM EvenementEquipes x WHERE " +
            "(x.id.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.dateDebut LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.dateFin LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.equipe.organisation.id IN :orgs)")
    List<Evenements> recherche(List<Long> orgs, String search, Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x.id.evenement) FROM EvenementEquipes x WHERE x.deleted = false AND x.id.equipe.organisation.id IN :orgs")
    Long countByOrganisation(List<Long> orgs);
    @Query("SELECT COUNT(DISTINCT x.id.evenement) FROM EvenementEquipes x WHERE " +
            "(x.id.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.dateDebut LIKE CONCAT('%',:search,'%') OR " +
            "x.id.evenement.dateFin LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.equipe.organisation.id IN :orgs)")
    Long countRecherche(List<Long> orgs, String search);
}
