package com.cyberethik.convocapi.persistance.service.dao;

import com.cyberethik.convocapi.persistance.entities.AccountOrganisationPK;
import com.cyberethik.convocapi.persistance.entities.AccountOrganisations;
import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountOrganisationDao {
    AccountOrganisations save(AccountOrganisations accountOrganisation);
    AccountOrganisations selectById(AccountOrganisationPK id);
    List<AccountOrganisations> selectByAccountAll(Accounts account);
    List<Organisations> selectByAccount(Accounts account);
    List<Long> selectByAccountIds(Accounts account);
    List<Organisations> selectByAccount(Accounts account, Pageable pageable);
    List<Organisations> recherche(Accounts account, String search, Pageable pageable);
    Long countByAccount(Accounts account);
    Long countRecherche(Accounts account, String search);
    void delete(AccountOrganisations accountOrganisation);
}
