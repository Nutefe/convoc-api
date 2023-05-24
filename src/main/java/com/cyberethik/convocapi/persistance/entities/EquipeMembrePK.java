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
public class EquipeMembrePK implements Serializable
{
    @JoinColumn(name = "equipe", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Equipes equipe;
    @JoinColumn(name = "membre", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Membres membre;

    public EquipeMembrePK() {
    }

    public EquipeMembrePK(Equipes equipe, Membres membre) {
        this.equipe = equipe;
        this.membre = membre;
    }

    public Equipes getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipes equipe) {
        this.equipe = equipe;
    }

    public Membres getMembre() {
        return membre;
    }

    public void setMembre(Membres membre) {
        this.membre = membre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipeMembrePK that = (EquipeMembrePK) o;
        return Objects.equals(equipe, that.equipe) && Objects.equals(membre, that.membre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipe, membre);
    }

    @Override
    public String toString() {
        return "EquipeMembrePK{" +
                "equipe=" + equipe +
                ", membre=" + membre +
                '}';
    }
}
