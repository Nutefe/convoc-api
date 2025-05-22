package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Roles;
import com.cyberethik.convocapi.persistance.repository.AccountRepository;
import com.cyberethik.convocapi.persistance.service.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AccountImp implements AccountDao {

    @Autowired
    private AccountRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Optional<Accounts> findById(Long id) {
        return repository.findById(id);
    }
    @Override
    public Accounts save(Accounts account) {
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        return repository.save(account);
    }

    @Override
    public Accounts update(Accounts account) {
        return repository.save(account);
    }

    @Override
    public Accounts selectById(Long id) {
        return repository.selectById(id);
    }

    @Override
    public Accounts selectByEmail(String email) {
        return repository.selectByEmail(email);
    }

    @Override
    public Accounts selectBySlug(String slug) {
        return repository.selectBySlug(slug);
    }

    @Override
    public Optional<Accounts> findByEmailAndActifTrueAndDeletedFalse(String email) {
        return repository.findByEmailAndActifTrueAndDeletedFalse(email);
    }

    @Override
    public List<Accounts> findByDeletedFalseOrderByIdDesc() {
        return repository.findByDeletedFalseOrderByIdDesc();
    }

    @Override
    public List<Accounts> findByDeletedTrueOrderByIdDesc() {
        return repository.findByDeletedTrueOrderByIdDesc();
    }

    @Override
    public List<Accounts> findByDeletedFalseOrderByIdDesc(Pageable pageable) {
        return repository.findByDeletedFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<Accounts> recherche(String search, Pageable pageable) {
        return repository.recherche(search, pageable);
    }

    @Override
    public Long countAccounts() {
        return repository.countAccounts();
    }

    @Override
    public Long countRecherche(String search) {
        return repository.countRecherche(search);
    }

    @Override
    public List<Accounts> findByRoleAndDeletedFalseOrderByIdDesc(Roles role) {
        return repository.findByRoleAndDeletedFalseOrderByIdDesc(role);
    }

    @Override
    public List<Accounts> findByRoleAndDeletedFalseOrderByIdDesc(Roles role, Pageable pageable) {
        return repository.findByRoleAndDeletedFalseOrderByIdDesc(role, pageable);
    }

    @Override
    public List<Accounts> recherche(Roles role, String search, Pageable pageable) {
        return repository.recherche(role, search, pageable);
    }

    @Override
    public Long countAccounts(Roles role) {
        return repository.countAccounts(role);
    }

    @Override
    public Long countRecherche(Roles role, String search) {
        return repository.countRecherche(role, search);
    }

    @Override
    public void updatePassword(Long id, String password) {

        repository.updatePassword(id, this.passwordEncoder.encode(password));
    }

    @Override
    public boolean existsByEmail(String email, Long id) {
        return repository.existsByEmail(email, id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public Long counts() {
        return repository.count();
    }

    @Override
    public void delete(Accounts account) {
        repository.delete(account);
    }

}
