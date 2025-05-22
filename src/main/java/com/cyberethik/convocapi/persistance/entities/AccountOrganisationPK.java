//
// Decompiled by Procyon v0.5.36
//
package com.cyberethik.convocapi.persistance.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AccountOrganisationPK implements Serializable
{
    @JoinColumn(name = "account", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Accounts account;
    @JoinColumn(name = "organisation", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organisations organisation;

    public AccountOrganisationPK() {
    }

    public AccountOrganisationPK(Accounts account, Organisations organisation) {
        this.account = account;
        this.organisation = organisation;
    }

    public Accounts getAccount() {
        return account;
    }

    public void setAccount(Accounts account) {
        this.account = account;
    }

    public Organisations getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisations organisation) {
        this.organisation = organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountOrganisationPK that = (AccountOrganisationPK) o;
        return Objects.equals(account, that.account) && Objects.equals(organisation, that.organisation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, organisation);
    }

    @Override
    public String toString() {
        return "AccountOrganisationPK{" +
                "account=" + account +
                ", organisation=" + organisation +
                '}';
    }
}
