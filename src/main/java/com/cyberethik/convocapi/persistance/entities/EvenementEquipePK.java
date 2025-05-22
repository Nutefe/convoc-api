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
public class EvenementEquipePK implements Serializable
{
    @JoinColumn(name = "evenement", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Evenements evenement;
    @JoinColumn(name = "equipe", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Equipes equipe;

    public EvenementEquipePK() {
    }

    public EvenementEquipePK(Evenements evenement, Equipes equipe) {
        this.evenement = evenement;
        this.equipe = equipe;
    }

    public Evenements getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenements evenement) {
        this.evenement = evenement;
    }

    public Equipes getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipes equipe) {
        this.equipe = equipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvenementEquipePK that = (EvenementEquipePK) o;
        return Objects.equals(evenement, that.evenement) && Objects.equals(equipe, that.equipe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evenement, equipe);
    }

    @Override
    public String toString() {
        return "EvenementEquipePK{" +
                "evenement=" + evenement +
                ", equipe=" + equipe +
                '}';
    }
}
