package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.EvenementEquipes;
import com.cyberethik.convocapi.persistance.entities.Membres;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrganisationDao {
    Optional<Organisations> findById(Long id);
    Organisations save(Organisations organisation);
    Organisations selectById(Long id);
    List<Organisations> findByDeletedFalseOrderByIdDesc();
    List<Organisations> findByDeletedTrueOrderByIdDesc();
    List<Organisations> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Organisations> recherche(String search, Pageable pageable);
    Long countOrganisations();
    Long countRecherche(String search);
    void delete(Organisations organisation);
}
