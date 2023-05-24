package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.EvenementEquipes;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResponsableDao {
    Optional<Responsables> findById(Long id);
    Responsables save(Responsables responsable);
    Responsables selectById(Long id);
    List<Responsables> findByDeletedFalseOrderByIdDesc();
    List<Responsables> findByDeletedTrueOrderByIdDesc();
    List<Responsables> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Responsables> recherche(String search, Pageable pageable);
    Long countResponsables();
    Long countRecherche(String search);
    void delete(Responsables responsable);
}
