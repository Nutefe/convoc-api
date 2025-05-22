package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.entities.Membres;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MembresRepository extends JpaRepository<Membres, Long> {
    @Query("SELECT x FROM Membres x WHERE x.deleted = false AND x.id=:id")
    Membres selectById(Long id);
    @Query("SELECT x FROM Membres x WHERE x.deleted = false AND x.slug=:slug")
    Membres selectBySlug(String slug);
    List<Membres> findByDeletedFalseOrderByIdDesc();

    List<Membres> findByDeletedTrueOrderByIdDesc();

    List<Membres> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT x FROM Membres x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR x.adresse LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    List<Membres> recherche(String search, Pageable pageable);

    @Query("SELECT COUNT(x) FROM Membres x WHERE x.deleted = false")
    Long countMembres();

    @Query("SELECT COUNT(x) FROM Membres x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR x.adresse LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    Long countRecherche(String search);

    @Query("SELECT DISTINCT x FROM Membres x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    List<Membres> selectByOrganisation(List<Long> orgs);
    @Query("SELECT x FROM Membres x WHERE x.deleted = false AND x.dateFin > :endDate AND x.organisation.id IN :orgs")
    List<Membres> selectByOrganisation(List<Long> orgs, Date endDate);
    @Query("SELECT x FROM Membres x WHERE x.deleted = false AND x.organisation.id IN :orgs AND x.dateFin > :endDate AND " +
            "x.id NOT IN (SELECT em.id.membre.id FROM EquipeMembres em WHERE em.id.equipe = :equipe)")
    List<Membres> selectByOrganisation(List<Long> orgs, Equipes equipe, Date endDate);
    @Query("SELECT DISTINCT x FROM Membres x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    List<Membres> selectByOrganisation(List<Long> orgs, Pageable pageable);
    @Query("SELECT DISTINCT x FROM Membres x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.responsable.adresse LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.organisation.id IN :orgs)")
    List<Membres> recherche(List<Long> orgs, String search, Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x) FROM Membres x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    Long countByOrganisation(List<Long> orgs);
    @Query("SELECT COUNT(DISTINCT x) FROM Membres x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.responsable.adresse LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRecherche(List<Long> orgs, String search);

    @Query("SELECT DISTINCT x FROM Membres x WHERE " +
            "(x.libelle IN :noms) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    List<Membres> recherche(List<Long> orgs, List<String> noms, Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x) FROM Membres x WHERE " +
            "(x.libelle IN :noms) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRecherche(List<Long> orgs, List<String> noms);
    @Query("SELECT DISTINCT x FROM Membres x WHERE " +
            "(x.deleted = false AND x.organisation.id IN :orgs)")
    List<Membres> recherche(List<Long> orgs, Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x) FROM Membres x WHERE " +
            "(x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRecherche(List<Long> orgs);
    @Query("SELECT DISTINCT x FROM Membres x WHERE " +
            "(x.libelle IN :noms OR x.dateFin > :endDate) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    List<Membres> rechercheActif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT x) FROM Membres x WHERE " +
            "(x.libelle IN :noms OR x.dateFin > :endDate) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRechercheActif(List<Long> orgs, List<String> noms, Date endDate);

    @Query("SELECT DISTINCT x FROM Membres x WHERE " +
            "(x.libelle IN :noms OR x.dateFin < :endDate) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    List<Membres> rechercheInactif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT x) FROM Membres x WHERE " +
            "(x.libelle IN :noms OR x.dateFin < :endDate) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRechercheInactif(List<Long> orgs, List<String> noms, Date endDate);



    @Query("SELECT CASE WHEN COUNT(slug) > 0 THEN true ELSE false END FROM Membres x WHERE x.slug = :slug")
    boolean existsBySlug(@Param("slug") final String slug);
    @Query("SELECT CASE WHEN COUNT(email) > 0 THEN true ELSE false END FROM Membres r WHERE r.email = :email and r.id != :id")
    boolean existsByEmail(@Param("email") final String email, @Param("id") final Long id);

    @Query("SELECT CASE WHEN COUNT(email) > 0 THEN true ELSE false END FROM Membres r WHERE r.email = :email")
    boolean existsByEmail(@Param("email") final String email);

}
