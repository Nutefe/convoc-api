package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.AccountOrganisationPK;
import com.cyberethik.convocapi.persistance.entities.AccountOrganisations;
import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import com.cyberethik.convocapi.persistance.repository.AccountOrganisationRepository;
import com.cyberethik.convocapi.persistance.service.dao.AccountOrganisationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountOrganisationImp implements AccountOrganisationDao {

    @Autowired
    private AccountOrganisationRepository repository;

    @Override
    public AccountOrganisations save(AccountOrganisations accountOrganisation) {
        return repository.save(accountOrganisation);
    }

    @Override
    public AccountOrganisations selectById(AccountOrganisationPK id) {
        return repository.selectById(id);
    }

    @Override
    public List<AccountOrganisations> selectByAccountAll(Accounts account) {
        return repository.selectByAccountAll(account);
    }

    @Override
    public List<Organisations> selectByAccount(Accounts account) {
        return repository.selectByAccount(account);
    }

    @Override
    public List<Long> selectByAccountIds(Accounts account) {
        return repository.selectByAccountIds(account);
    }

    @Override
    public List<Organisations> selectByAccount(Accounts account, Pageable pageable) {
        return repository.selectByAccount(account, pageable);
    }

    @Override
    public List<Organisations> recherche(Accounts account, String search, Pageable pageable) {
        return repository.recherche(account, search, pageable);
    }

    @Override
    public Long countByAccount(Accounts account) {
        return repository.countByAccount(account);
    }

    @Override
    public Long countRecherche(Accounts account, String search) {
        return repository.countRecherche(account, search);
    }

    @Override
    public void delete(AccountOrganisations accountOrganisation) {
        repository.delete(accountOrganisation);
    }
}
