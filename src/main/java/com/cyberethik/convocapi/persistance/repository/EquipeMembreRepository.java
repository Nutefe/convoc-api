package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipeMembreRepository extends JpaRepository<EquipeMembres, EquipeMembrePK> {
    @Query("SELECT x FROM EquipeMembres x WHERE x.deleted = false AND x.id=:id")
    EquipeMembres selectById(EquipeMembrePK id);
    @Query("SELECT x.id.membre FROM EquipeMembres x WHERE x.deleted = false AND x.id.equipe=:equipe")
    List<Membres> selectByEquipe(Equipes equipe);
    @Query("SELECT x FROM EquipeMembres x WHERE x.deleted = false AND x.id.membre=:membre")
    List<EquipeMembres> selectByEquipe(Membres membre);
    @Query("SELECT x.id.membre FROM EquipeMembres x WHERE x.deleted = false AND x.id.equipe=:equipe")
    List<Membres> selectByEquipe(Equipes equipe, Pageable pageable);
    @Query("SELECT x.id.membre FROM EquipeMembres x WHERE " +
            "(x.id.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.adresse LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.equipe=:equipe)")
    List<Membres> recherche(Equipes equipe, String search, Pageable pageable);
    @Query("SELECT COUNT(x.id.membre) FROM EquipeMembres x WHERE x.deleted = false AND x.id.equipe=:equipe")
    Long countByEquipe(Equipes equipe);
    @Query("SELECT COUNT(x.id.membre) FROM EquipeMembres x WHERE " +
            "(x.id.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.adresse LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.equipe=:equipe)")
    Long countRecherche(Equipes equipe, String search);

    @Query("SELECT x.id.membre FROM EquipeMembres x WHERE x.deleted = false AND x.id.equipe.organisation.id IN :orgs")
    List<Membres> selectByOrganisation(List<Long> orgs);
    @Query("SELECT x.id.membre FROM EquipeMembres x WHERE x.deleted = false AND x.id.equipe.organisation.id IN :orgs")
    List<Membres> selectByOrganisation(List<Long> orgs, Pageable pageable);
    @Query("SELECT x.id.membre FROM EquipeMembres x WHERE " +
            "(x.id.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.adresse LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.equipe.organisation.id IN :orgs)")
    List<Membres> recherche(List<Long> orgs, String search, Pageable pageable);
    @Query("SELECT COUNT(x.id.membre) FROM EquipeMembres x WHERE x.deleted = false AND x.id.equipe.organisation.id IN :orgs")
    Long countByOrganisation(List<Long> orgs);
    @Query("SELECT COUNT(x.id.membre) FROM EquipeMembres x WHERE " +
            "(x.id.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.adresse LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.equipe.organisation.id IN :orgs)")
    Long countRecherche(List<Long> orgs, String search);

    @Query("SELECT x.id.membre.responsable FROM EquipeMembres x WHERE x.deleted = false AND x.id.equipe.organisation.id IN :orgs")
    List<Responsables> selectByOrganisationRes(List<Long> orgs);
    @Query("SELECT x.id.membre.responsable FROM EquipeMembres x WHERE x.deleted = false AND x.id.equipe.organisation.id IN :orgs")
    List<Responsables> selectByOrganisationRes(List<Long> orgs, Pageable pageable);
    @Query("SELECT x.id.membre.responsable FROM EquipeMembres x WHERE " +
            "(x.id.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.adresse LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.equipe.organisation.id IN :orgs)")
    List<Responsables> rechercheRes(List<Long> orgs, String search, Pageable pageable);
    @Query("SELECT COUNT(x.id.membre.responsable) FROM EquipeMembres x WHERE x.deleted = false AND x.id.equipe.organisation.id IN :orgs")
    Long countByOrganisationRes(List<Long> orgs);
    @Query("SELECT COUNT(x.id.membre.responsable) FROM EquipeMembres x WHERE " +
            "(x.id.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.id.membre.responsable.adresse LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.equipe.organisation.id IN :orgs)")
    Long countRechercheRes(List<Long> orgs, String search);
}
