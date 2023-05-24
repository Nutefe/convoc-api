package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.EvenementEquipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MembreDao {
    Optional<Membres> findById(Long id);
    Membres save(Membres membre);
    Membres selectById(Long id);
    Membres selectBySlug(String slug);
    List<Membres> findByDeletedFalseOrderByIdDesc();
    List<Membres> findByDeletedTrueOrderByIdDesc();
    List<Membres> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Membres> recherche(String search, Pageable pageable);
    Long countMembres();
    Long countRecherche(String search);
    boolean existsBySlug(final String slug);
    void delete(Membres membre);
}
