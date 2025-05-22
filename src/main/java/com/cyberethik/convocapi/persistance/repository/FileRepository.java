package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Files;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<Files, Long> {
    @Query("SELECT r FROM Files r WHERE r.deleted = false AND r.id=:id")
    Files selectById(Long id);

    List<Files> findByDeletedFalseOrderByIdDesc();

    List<Files> findByDeletedTrueOrderByIdDesc();

    List<Files> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT r FROM Files r WHERE r.name LIKE CONCAT('%',:search,'%') AND r.deleted = false")
    List<Files> recherche(String search, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Files r WHERE r.deleted = false")
    Long countFiles();

    @Query("SELECT COUNT(r) FROM Files r WHERE r.name LIKE CONCAT('%',:search,'%') AND r.deleted = false")
    Long countRecherche(String search);
}
