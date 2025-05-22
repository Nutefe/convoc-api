package com.cyberethik.convocapi.persistance.service.dao;


import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Roles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AccountDao {

    Optional<Accounts> findById(Long id);
    Accounts save(Accounts account);
    Accounts update(Accounts account);
    Accounts selectById(Long id);
    Accounts selectByEmail(String email);
    Accounts selectBySlug(String slug);
    Optional<Accounts> findByEmailAndActifTrueAndDeletedFalse(String email);
    List<Accounts> findByDeletedFalseOrderByIdDesc();
    List<Accounts> findByDeletedTrueOrderByIdDesc();
    List<Accounts> findByDeletedFalseOrderByIdDesc(Pageable pageable);
    List<Accounts> recherche(String search, Pageable pageable);
    Long countAccounts();
    Long countRecherche(String search);
    List<Accounts> findByRoleAndDeletedFalseOrderByIdDesc(Roles role);
    List<Accounts> findByRoleAndDeletedFalseOrderByIdDesc(Roles role, Pageable pageable);
    List<Accounts> recherche(Roles role, String search, Pageable pageable);
    Long countAccounts(Roles role);
    Long countRecherche(Roles role, String search);
    void updatePassword(final Long id, final String password);
    boolean existsByEmail(final String email, final Long id);
    boolean existsByEmail(final String email);
    boolean existsBySlug(final String slug);
    Long counts();
    void delete(Accounts account);
}
