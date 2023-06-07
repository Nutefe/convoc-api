package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EquipeRepository extends JpaRepository<Equipes, Long> {
    @Query("SELECT x FROM Equipes x WHERE x.deleted = false AND x.id=:id")
    Equipes selectById(Long id);

    List<Equipes> findByDeletedFalseOrderByIdDesc();

    List<Equipes> findByDeletedTrueOrderByIdDesc();

    List<Equipes> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT x FROM Equipes x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.organisation.nom LIKE CONCAT('%',:search,'%') OR x.organisation.desciption LIKE CONCAT('%',:search,'%') OR " +
            "x.organisation.devise LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    List<Equipes> recherche(String search, Pageable pageable);

    @Query("SELECT COUNT(x) FROM Equipes x WHERE x.deleted = false")
    Long countEquipes();

    @Query("SELECT COUNT(x) FROM Equipes x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.organisation.nom LIKE CONCAT('%',:search,'%') OR x.organisation.desciption LIKE CONCAT('%',:search,'%') OR " +
            "x.organisation.devise LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    Long countRecherche(String search);

    @Query("SELECT DISTINCT x FROM Equipes x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    List<Equipes> seletByOrganisation(List<Long> orgs);
    @Query("SELECT DISTINCT x FROM Equipes x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    List<Equipes> seletByOrganisation(List<Long> orgs, Pageable pageable);

    @Query("SELECT DISTINCT x FROM Equipes x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.organisation.nom LIKE CONCAT('%',:search,'%') OR x.organisation.desciption LIKE CONCAT('%',:search,'%') OR " +
            "x.organisation.devise LIKE CONCAT('%',:search,'%')) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    List<Equipes> recherche(List<Long> orgs, String search, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT x) FROM Equipes x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    Long countOrganisation(List<Long> orgs);

    @Query("SELECT COUNT(DISTINCT x) FROM Equipes x WHERE " +
            "(x.libelle LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.organisation.nom LIKE CONCAT('%',:search,'%') OR x.organisation.desciption LIKE CONCAT('%',:search,'%') OR " +
            "x.organisation.devise LIKE CONCAT('%',:search,'%')) AND (x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRecherche(List<Long> orgs, String search);

    @Query("SELECT COUNT(x) FROM Equipes x WHERE x.deleted = false AND x.organisation.id = :orgs")
    Long countByOrganisation(Long orgs);
}
