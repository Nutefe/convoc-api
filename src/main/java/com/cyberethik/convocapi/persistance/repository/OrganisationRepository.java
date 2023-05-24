package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Organisations;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrganisationRepository extends JpaRepository<Organisations, Long> {
    @Query("SELECT x FROM Organisations x WHERE x.deleted = false AND x.id=:id")
    Organisations selectById(Long id);

    List<Organisations> findByDeletedFalseOrderByIdDesc();

    List<Organisations> findByDeletedTrueOrderByIdDesc();

    List<Organisations> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT x FROM Organisations x WHERE " +
            "(x.nom LIKE CONCAT('%',:search,'%') OR x.desciption LIKE CONCAT('%',:search,'%') OR " +
            "x.devise LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    List<Organisations> recherche(String search, Pageable pageable);

    @Query("SELECT COUNT(x) FROM Organisations x WHERE x.deleted = false")
    Long countOrganisations();

    @Query("SELECT COUNT(x) FROM Organisations x WHERE " +
            "(x.nom LIKE CONCAT('%',:search,'%') OR x.desciption LIKE CONCAT('%',:search,'%') OR " +
            "x.devise LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    Long countRecherche(String search);
}
