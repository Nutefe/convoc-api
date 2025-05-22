package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.EvenementEquipes;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResponsableDao {
    Optional<Responsables> findById(Long id);
    Responsables selectByEmail(String email);
    Responsables save(Responsables responsable);
    Responsables selectById(Long id);
    List<Responsables> findByDeletedFalseOrderByIdDesc();
    List<Responsables> findByDeletedTrueOrderByIdDesc();
    List<Responsables> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Responsables> recherche(String search, Pageable pageable);
    Long countResponsables();
    Long countRecherche(String search);
    void delete(Responsables responsable);
    List<Responsables> seletByOrganisation(List<Long> orgs);
    List<Responsables> seletByOrganisation(List<Long> orgs, Pageable pageable);
    List<Responsables> recherche(List<Long> orgs, String search, Pageable pageable);
    Long countOrganisation(List<Long> orgs);
    Long countRecherche(List<Long> orgs, String search);
    boolean existsByEmail(final String email, final Long id);
    boolean existsByEmail(final String email);
}
