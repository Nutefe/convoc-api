package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResponsableRepository extends JpaRepository<Responsables, Long> {
    @Query("SELECT x FROM Responsables x WHERE x.deleted = false AND x.id=:id")
    Responsables selectById(Long id);
    @Query("SELECT r FROM Responsables r WHERE r.email=:email")
    Responsables selectByEmail(String email);
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

    @Query("SELECT DISTINCT x FROM Responsables x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    List<Responsables> seletByOrganisation(List<Long> orgs);
    @Query("SELECT DISTINCT x FROM Responsables x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    List<Responsables> seletByOrganisation(List<Long> orgs, Pageable pageable);

    @Query("SELECT DISTINCT x FROM Responsables x WHERE " +
            "(x.email LIKE CONCAT('%',:search,'%') OR x.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.telephone LIKE CONCAT('%',:search,'%') OR x.adresse LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.organisation.id IN :orgs)")
    List<Responsables> recherche(List<Long> orgs, String search, Pageable pageable);

    @Query("SELECT COUNT(DISTINCT x) FROM Responsables x WHERE x.deleted = false AND x.organisation.id IN :orgs")
    Long countOrganisation(List<Long> orgs);

    @Query("SELECT COUNT(DISTINCT x) FROM Responsables x WHERE " +
            "(x.email LIKE CONCAT('%',:search,'%') OR x.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.telephone LIKE CONCAT('%',:search,'%') OR x.adresse LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.organisation.id IN :orgs)")
    Long countRecherche(List<Long> orgs, String search);
    @Query("SELECT CASE WHEN COUNT(email) > 0 THEN true ELSE false END FROM Responsables r WHERE r.email = :email and r.id != :id")
    boolean existsByEmail(@Param("email") final String email, @Param("id") final Long id);

    @Query("SELECT CASE WHEN COUNT(email) > 0 THEN true ELSE false END FROM Responsables r WHERE r.email = :email")
    boolean existsByEmail(@Param("email") final String email);
}
