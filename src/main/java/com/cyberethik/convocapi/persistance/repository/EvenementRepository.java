package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenements, Long> {
    @Query("SELECT x FROM Evenements x WHERE x.deleted = false AND x.id=:id")
    Evenements selectById(Long id);

    List<Evenements> findByDeletedFalseOrderByIdDesc();

    List<Evenements> findByDeletedTrueOrderByIdDesc();

    List<Evenements> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT x FROM Evenements x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.dateDebut LIKE CONCAT('%',:search,'%') OR x.dateFin LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    List<Evenements> recherche(String search, Pageable pageable);

    @Query("SELECT COUNT(x) FROM Evenements x WHERE x.deleted = false")
    Long countEvenements();

    @Query("SELECT COUNT(x) FROM Evenements x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.dateDebut LIKE CONCAT('%',:search,'%') OR x.dateFin LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    Long countRecherche(String search);


    @Query("SELECT COUNT(x) FROM Evenements x WHERE x.deleted = false AND x.organisation.id = :org AND x.dateFin > :end")
    Long countEvenements(Long org, Date end);

    @Query("SELECT DISTINCT x FROM Evenements x WHERE " +
            "(x.libelle IN :noms) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    List<Evenements> recherche(List<Long> orgs, List<String> noms, Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x) FROM Evenements x WHERE " +
            "(x.libelle IN :noms) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRecherche(List<Long> orgs, List<String> noms);
    @Query("SELECT DISTINCT x FROM Evenements x WHERE " +
            "(x.deleted = false AND x.organisation.id IN :orgs)")
    List<Evenements> recherche(List<Long> orgs, Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x) FROM Evenements x WHERE " +
            "(x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRecherche(List<Long> orgs);
    @Query("SELECT DISTINCT x FROM Evenements x WHERE " +
            "(x.libelle IN :noms OR x.dateFin > :endDate) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    List<Evenements> rechercheActif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x) FROM Evenements x WHERE " +
            "(x.libelle IN :noms OR x.dateFin > :endDate) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRechercheActif(List<Long> orgs, List<String> noms, Date endDate);
    @Query("SELECT DISTINCT x FROM Evenements x WHERE " +
            "(x.libelle IN :noms OR x.dateFin < :endDate) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    List<Evenements> rechercheInactif(List<Long> orgs, List<String> noms, Date endDate, Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x) FROM Evenements x WHERE " +
            "(x.libelle IN :noms OR x.dateFin < :endDate) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRechercheInactif(List<Long> orgs, List<String> noms, Date endDate);

    @Query("SELECT DISTINCT x FROM Evenements x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    List<Evenements> selectByOrganisation(List<Long> orgs, Pageable pageable);
    @Query("SELECT DISTINCT x FROM Evenements x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    List<Evenements> selectByOrganisation(List<Long> orgs);
    @Query("SELECT DISTINCT x FROM Evenements x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.dateDebut LIKE CONCAT('%',:search,'%') OR " +
            "x.dateFin LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.organisation.id IN :orgs)")
    List<Evenements> recherche(List<Long> orgs, String search, Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x) FROM Evenements x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    Long countByOrganisation(List<Long> orgs);
    @Query("SELECT COUNT(DISTINCT x) FROM Evenements x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.dateDebut LIKE CONCAT('%',:search,'%') OR " +
            "x.dateFin LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRecherche(List<Long> orgs, String search);
}
