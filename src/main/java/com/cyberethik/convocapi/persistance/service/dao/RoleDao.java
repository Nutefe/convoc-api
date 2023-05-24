package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Roles;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoleDao {
    Optional<Roles> findById(Integer id);
    Roles save(Roles role);
    Roles selectById(Integer id);
    List<Roles> findByDeletedFalseOrderByIdDesc();
    List<Roles> findByDeletedTrueOrderByIdDesc();
    List<Roles> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Roles> recherche(String search, Pageable pageable);
    Long countRoles();
    Long count();
    Long countRecherche(String search);
    void delete(Roles role);
}
