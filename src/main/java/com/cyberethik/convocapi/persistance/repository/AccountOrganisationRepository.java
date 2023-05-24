package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountOrganisationRepository extends JpaRepository<AccountOrganisations, AccountOrganisationPK> {
    @Query("SELECT x FROM AccountOrganisations x WHERE x.deleted = false AND x.id=:id")
    AccountOrganisations selectById(AccountOrganisationPK id);
    @Query("SELECT x FROM AccountOrganisations x WHERE x.deleted = false AND x.id.account=:account")
    List<AccountOrganisations> selectByAccountAll(Accounts account);
    @Query("SELECT x.id.organisation FROM AccountOrganisations x WHERE x.deleted = false AND x.id.account=:account")
    List<Organisations> selectByAccount(Accounts account);
    @Query("SELECT x.id.organisation.id FROM AccountOrganisations x WHERE x.deleted = false AND x.id.account=:account")
    List<Long> selectByAccountIds(Accounts account);
    @Query("SELECT x.id.organisation FROM AccountOrganisations x WHERE x.deleted = false AND x.id.account=:account")
    List<Organisations> selectByAccount(Accounts account, Pageable pageable);
    @Query("SELECT x.id.organisation FROM AccountOrganisations x WHERE (x.id.organisation.nom LIKE CONCAT('%',:search,'%') OR " +
            "x.id.organisation.desciption LIKE CONCAT('%',:search,'%') OR " +
            "x.id.organisation.devise LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.account=:account)")
    List<Organisations> recherche(Accounts account, String search, Pageable pageable);
    @Query("SELECT COUNT(x.id.organisation) FROM AccountOrganisations x WHERE x.deleted = false AND x.id.account=:account")
    Long countByAccount(Accounts account);
    @Query("SELECT COUNT(x.id.organisation) FROM AccountOrganisations x WHERE (x.id.organisation.nom LIKE CONCAT('%',:search,'%') OR " +
            "x.id.organisation.desciption LIKE CONCAT('%',:search,'%') OR " +
            "x.id.organisation.devise LIKE CONCAT('%',:search,'%')) AND " +
            "(x.deleted = false AND x.id.account=:account)")
    Long countRecherche(Accounts account, String search);

}
