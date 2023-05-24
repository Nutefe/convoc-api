package com.cyberethik.convocapi.persistance.repository;


import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Roles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Accounts, Long> {
    @Query("SELECT r FROM Accounts r WHERE r.deleted = false AND r.id=:id")
    Accounts selectById(Long id);
    @Query("SELECT r FROM Accounts r WHERE r.deleted = false AND r.email=:email")
    Accounts selectByEmail(String email);
    @Query("SELECT r FROM Accounts r WHERE r.deleted = false AND r.slug=:slug")
    Accounts selectBySlug(String slug);
    Optional<Accounts> findByEmailAndActifTrueAndDeletedFalse(String email);

    List<Accounts> findByDeletedFalseOrderByIdDesc();

    List<Accounts> findByDeletedTrueOrderByIdDesc();

    List<Accounts> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT r FROM Accounts r WHERE " +
            "(r.email LIKE CONCAT('%',:search,'%') OR r.libelle LIKE CONCAT('%',:search,'%') OR r.role.libelle LIKE CONCAT('%',:search,'%'))" +
            " AND r.deleted = false")
    List<Accounts> recherche(String search, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Accounts r WHERE r.deleted = false")
    Long countAccounts();

    @Query("SELECT COUNT(r) FROM Accounts r WHERE " +
            "(r.email LIKE CONCAT('%',:search,'%') OR r.libelle LIKE CONCAT('%',:search,'%') OR r.role.libelle LIKE CONCAT('%',:search,'%')) " +
            "AND r.deleted = false")
    Long countRecherche(String search);
    List<Accounts> findByRoleAndDeletedFalseOrderByIdDesc(Roles role);
    List<Accounts> findByRoleAndDeletedFalseOrderByIdDesc(Roles role, Pageable pageable);

    @Query("SELECT r FROM Accounts r WHERE " +
            "(r.email LIKE CONCAT('%',:search,'%') OR r.libelle LIKE CONCAT('%',:search,'%')) AND " +
            "(r.deleted = false AND r.role = :role)")
    List<Accounts> recherche(Roles role, String search, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Accounts r WHERE r.deleted = false AND r.role = :role")
    Long countAccounts(Roles role);

    @Query("SELECT COUNT(r) FROM Accounts r WHERE (r.email LIKE CONCAT('%',:search,'%') OR r.libelle LIKE CONCAT('%',:search,'%')) AND (r.deleted = false AND r.role = :role)")
    Long countRecherche(Roles role, String search);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Accounts r set r.password = :password where r.id = :id")
    void updatePassword(@Param("id") final Long id, @Param("password") final String password);

    @Query("SELECT CASE WHEN COUNT(email) > 0 THEN true ELSE false END FROM Accounts r WHERE r.email = :email and r.id != :id")
    boolean existsByEmail(@Param("email") final String email, @Param("id") final Long id);

    @Query("SELECT CASE WHEN COUNT(email) > 0 THEN true ELSE false END FROM Accounts r WHERE r.email = :email")
    boolean existsByEmail(@Param("email") final String email);
    @Query("SELECT CASE WHEN COUNT(slug) > 0 THEN true ELSE false END FROM Accounts r WHERE r.slug = :slug")
    boolean existsBySlug(@Param("slug") final String slug);
}
