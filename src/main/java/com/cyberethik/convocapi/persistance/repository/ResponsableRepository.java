package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Responsables;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResponsableRepository extends JpaRepository<Responsables, Long> {
    @Query("SELECT x FROM Responsables x WHERE x.deleted = false AND x.id=:id")
    Responsables selectById(Long id);

    List<Responsables> findByDeletedFalseOrderByIdDesc();

    List<Responsables> findByDeletedTrueOrderByIdDesc();

    List<Responsables> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT x FROM Responsables x WHERE " +
            "(x.email LIKE CONCAT('%',:search,'%') OR x.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.telephone LIKE CONCAT('%',:search,'%') OR x.adresse LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    List<Responsables> recherche(String search, Pageable pageable);

    @Query("SELECT COUNT(x) FROM Responsables x WHERE x.deleted = false")
    Long countResponsables();

    @Query("SELECT COUNT(x) FROM Responsables x WHERE " +
            "(x.email LIKE CONCAT('%',:search,'%') OR x.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.telephone LIKE CONCAT('%',:search,'%') OR x.adresse LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    Long countRecherche(String search);
}
