package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.EvenementEquipePK;
import com.cyberethik.convocapi.persistance.entities.EvenementEquipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.repository.EquipeMembreRepository;
import com.cyberethik.convocapi.persistance.repository.EvenementEquipeRepository;
import com.cyberethik.convocapi.persistance.service.dao.EvenementEquipeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvenementEquipeImp implements EvenementEquipeDao
{
    @Autowired
    private EvenementEquipeRepository repository;

    @Override
    public EvenementEquipes save(EvenementEquipes evenementEquipe) {
        return repository.save(evenementEquipe);
    }

    @Override
    public EvenementEquipes selectById(EvenementEquipePK id) {
        return repository.selectById(id);
    }

    @Override
    public List<EvenementEquipes> selectByEquipe(Evenements evenement) {
        return repository.selectByEquipe(evenement);
    }

    @Override
    public List<Evenements> selectByEquipe(Equipes equipe) {
        return repository.selectByEquipe(equipe);
    }

    @Override
    public List<Evenements> selectByEquipe(Equipes equipe, Pageable pageable) {
        return repository.selectByEquipe(equipe, pageable);
    }

    @Override
    public List<Evenements> recherche(Equipes equipe, String search, Pageable pageable) {
        return repository.recherche(equipe, search, pageable);
    }

    @Override
    public Long countByEquipe(Equipes equipe) {
        return repository.countByEquipe(equipe);
    }

    @Override
    public Long countRecherche(Equipes equipe, String search) {
        return repository.countRecherche(equipe, search);
    }

    @Override
    public List<Evenements> selectByOrganisation(List<Long> orgs) {
        return repository.selectByOrganisation(orgs);
    }

    @Override
    public List<Long> selectByOrganisationIds(List<Long> orgs) {
        return repository.selectByOrganisationIds(orgs);
    }

    @Override
    public List<Evenements> selectByOrganisation(List<Long> orgs, Pageable pageable) {
        return repository.selectByOrganisation(orgs, pageable);
    }

    @Override
    public List<Evenements> recherche(List<Long> orgs, String search, Pageable pageable) {
        return repository.recherche(orgs, search, pageable);
    }

    @Override
    public Long countByOrganisation(List<Long> orgs) {
        return repository.countByOrganisation(orgs);
    }

    @Override
    public Long countRecherche(List<Long> orgs, String search) {
        return repository.countRecherche(orgs, search);
    }

    @Override
    public void delete(EvenementEquipes evenementEquipe) {
        repository.delete(evenementEquipe);
    }

    @Override
    public void delete(List<EvenementEquipes> evenementEquipes) {
        repository.deleteAll(evenementEquipes);
    }
}
