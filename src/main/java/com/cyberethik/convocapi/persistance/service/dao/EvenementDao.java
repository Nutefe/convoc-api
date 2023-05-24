package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EvenementDao {
    Optional<Evenements> findById(Long id);
    Evenements save(Evenements evenement);
    Evenements selectById(Long id);
    List<Evenements> findByDeletedFalseOrderByIdDesc();
    List<Evenements> findByDeletedTrueOrderByIdDesc();
    List<Evenements> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Evenements> recherche(String search, Pageable pageable);
    Long countEvenements();
    Long countRecherche(String search);
    void delete(Evenements evenement);
}
