package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.Files;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FileDao {

    Optional<Files> findById(Long id);
    Files save(Files file);
    Files selectById(Long id);
    List<Files> findByDeletedFalseOrderByIdDesc();
    List<Files> findByDeletedTrueOrderByIdDesc();
    List<Files> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Files> recherche(String search, Pageable pageable);
    Long counts();
    Long countFiles();
    Long countRecherche(String search);
}
