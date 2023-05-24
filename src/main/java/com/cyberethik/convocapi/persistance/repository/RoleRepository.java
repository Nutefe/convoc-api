package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Roles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Roles, Integer> {
    @Query("SELECT r FROM Roles r WHERE r.deleted = false AND r.id=:id")
    Roles selectById(Integer id);

    List<Roles> findByDeletedFalseOrderByIdDesc();

    List<Roles> findByDeletedTrueOrderByIdDesc();

    List<Roles> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT r FROM Roles r WHERE r.libelle LIKE CONCAT('%',:search,'%') AND r.deleted = false")
    List<Roles> recherche(String search, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Roles r WHERE r.deleted = false")
    Long countRoles();

    @Query("SELECT COUNT(r) FROM Roles r WHERE r.libelle LIKE CONCAT('%',:search,'%') AND r.deleted = false")
    Long countRecherche(String search);
}
