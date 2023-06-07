package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
import com.cyberethik.convocapi.persistance.entities.Membres;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT CASE WHEN COUNT(slug) > 0 THEN true ELSE false END FROM Membres x WHERE x.slug = :slug")
    boolean existsBySlug(@Param("slug") final String slug);

}
