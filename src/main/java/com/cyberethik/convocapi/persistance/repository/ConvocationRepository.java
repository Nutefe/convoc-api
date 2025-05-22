package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Convocations;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ConvocationRepository extends JpaRepository<Convocations, Long> {

    @Query("SELECT x FROM Convocations x WHERE x.deleted = false AND x.id=:id")
    Convocations selectById(Long id);

    @Query("SELECT x FROM Convocations x WHERE x.deleted = false AND x.slug=:slug")
    Convocations selectBySlug(String slug);
    List<Convocations> findByDeletedFalseOrderByIdDesc();

    List<Convocations> findByDeletedTrueOrderByIdDesc();

    Convocations findTop1ByEvenementAndDeletedFalse(Evenements evenement);
    List<Convocations> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT x FROM Convocations x WHERE " +
            "(x.dateEnvoi LIKE CONCAT('%',:search,'%') OR x.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.membre.libelle LIKE CONCAT('%',:search,'%') OR x.membre.responsable.libelle LIKE CONCAT('%',:search,'%')) " +
            "AND x.deleted = false")
    List<Convocations> recherche(String search, Pageable pageable);

    @Query("SELECT COUNT(x) FROM Convocations x WHERE x.deleted = false")
    Long countConvocations();

    @Query("SELECT COUNT(x) FROM Convocations x WHERE " +
            "(x.dateEnvoi LIKE CONCAT('%',:search,'%') OR x.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.membre.libelle LIKE CONCAT('%',:search,'%') OR x.membre.responsable.libelle LIKE CONCAT('%',:search,'%')) " +
            "AND x.deleted = false")
    Long countRecherche(String search);

    @Query("SELECT x FROM Convocations x WHERE x.deleted = false AND x.evenement = :evenement")
    List<Convocations> selectByEvenement(Evenements evenement, Pageable pageable);

    @Query("SELECT x FROM Convocations x WHERE " +
            "(x.dateEnvoi LIKE CONCAT('%',:search,'%') OR x.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.membre.libelle LIKE CONCAT('%',:search,'%') OR x.membre.responsable.libelle LIKE CONCAT('%',:search,'%')) " +
            "AND (x.deleted = false  AND x.evenement = :evenement)")
    List<Convocations> recherche(Evenements evenement, String search, Pageable pageable);

    @Query("SELECT COUNT(x) FROM Convocations x WHERE x.deleted = false AND x.evenement = :evenement")
    Long countConvocations(Evenements evenement);

    @Query("SELECT COUNT(x) FROM Convocations x WHERE " +
            "(x.dateEnvoi LIKE CONCAT('%',:search,'%') OR x.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.membre.libelle LIKE CONCAT('%',:search,'%') OR x.membre.responsable.libelle LIKE CONCAT('%',:search,'%')) " +
            "AND (x.deleted = false AND x.evenement = :evenement)")
    Long countRecherche(Evenements evenement, String search);
    @Query("SELECT x.membre FROM Convocations x WHERE x.deleted = false AND x.evenement = :evenement")
    List<Membres> selectMembreByEvenement(Evenements evenement, Pageable pageable);
    @Query("SELECT DISTINCT x.membre FROM Convocations x WHERE x.deleted = false AND x.evenement = :evenement")
    List<Membres> selectMembreByEvenement(Evenements evenement);

    @Query("SELECT x.membre FROM Convocations x WHERE " +
            "(x.dateEnvoi LIKE CONCAT('%',:search,'%') OR x.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.membre.libelle LIKE CONCAT('%',:search,'%') OR x.membre.responsable.libelle LIKE CONCAT('%',:search,'%')) " +
            "AND (x.deleted = false  AND x.evenement = :evenement)")
    List<Membres> rechercheMembre(Evenements evenement, String search, Pageable pageable);

    @Query("SELECT COUNT(x.membre) FROM Convocations x WHERE x.deleted = false AND x.evenement = :evenement")
    Long countConvocationsMembre(Evenements evenement);

    @Query("SELECT COUNT(x.membre) FROM Convocations x WHERE " +
            "(x.dateEnvoi LIKE CONCAT('%',:search,'%') OR x.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.membre.libelle LIKE CONCAT('%',:search,'%') OR x.membre.responsable.libelle LIKE CONCAT('%',:search,'%')) " +
            "AND (x.deleted = false AND x.evenement = :evenement)")
    Long countRechercheMembre(Evenements evenement, String search);
    @Query("SELECT DISTINCT x FROM Convocations x WHERE x.deleted = false AND x.evenement.id IN :events")
    List<Convocations> selectByEvenement(List<Long> events, Pageable pageable);

    @Query("SELECT DISTINCT x FROM Convocations x WHERE " +
            "(x.dateEnvoi LIKE CONCAT('%',:search,'%') OR x.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.membre.libelle LIKE CONCAT('%',:search,'%') OR x.membre.responsable.libelle LIKE CONCAT('%',:search,'%')) " +
            "AND (x.deleted = false  AND x.evenement.id IN :events)")
    List<Convocations> recherche(List<Long> events, String search, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT x) FROM Convocations x WHERE x.deleted = false AND x.evenement.id IN :events")
    Long countConvocations(List<Long> events);

    @Query("SELECT COUNT(DISTINCT x) FROM Convocations x WHERE " +
            "(x.dateEnvoi LIKE CONCAT('%',:search,'%') OR x.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.evenement.description LIKE CONCAT('%',:search,'%') OR " +
            "x.membre.libelle LIKE CONCAT('%',:search,'%') OR x.membre.responsable.libelle LIKE CONCAT('%',:search,'%')) " +
            "AND (x.deleted = false AND x.evenement.id IN :events)")
    Long countRecherche(List<Long> events, String search);

    @Query("SELECT CASE WHEN COUNT(slug) > 0 THEN true ELSE false END FROM Convocations x WHERE x.slug = :slug")
    boolean existsBySlug(@Param("slug") final String slug);

    @Query("SELECT COUNT(DISTINCT x.evenement.id) FROM Convocations x WHERE x.deleted = false AND x.evenement.organisation.id = :org AND x.dateEnvoi BETWEEN :startDate AND :endDate")
    Long countConvocations(Long org, Date startDate, Date endDate);
    @Query("SELECT DISTINCT x.evenement FROM Convocations x WHERE x.deleted = false AND x.evenement.organisation.id = :org AND x.dateEnvoi BETWEEN :startDate AND :endDate")
    List<Evenements> selectConvocations(Long org, Date startDate, Date endDate);

    @Query("SELECT DISTINCT x.membre FROM Convocations x WHERE " +
            "(x.membre.libelle IN :membres OR x.membre.id IN (SELECT em.id.membre.id FROM EquipeMembres em WHERE em.id.equipe.libelle IN :equipes)) " +
            "AND (x.deleted = false  AND x.evenement = :evenement)")
    List<Membres> rechercheMembre(Evenements evenement,
                                  List<String> equipes,
                                  List<String> membres,
                                  Pageable pageable);
    @Query("SELECT COUNT(DISTINCT x.membre) FROM Convocations x WHERE " +
            "(x.membre.libelle IN :membres OR x.membre.id IN (SELECT em.id.membre.id FROM EquipeMembres em WHERE em.id.equipe.libelle IN :equipes)) " +
            "AND (x.deleted = false  AND x.evenement = :evenement)")
    Long countRechercheMembre(Evenements evenement, List<String> equipes,
                              List<String> membres);

}
