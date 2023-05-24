package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
