package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.repository.EquipeMembreRepository;
import com.cyberethik.convocapi.persistance.service.dao.EquipeMembreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipeMembreImp implements EquipeMembreDao {

    @Autowired
    private EquipeMembreRepository repository;

    @Override
    public EquipeMembres save(EquipeMembres equipeMembre) {
        return repository.save(equipeMembre);
    }

    @Override
    public EquipeMembres selectById(EquipeMembrePK id) {
        return repository.selectById(id);
    }

    @Override
    public List<EquipeMembres> selectByEquipe(Membres membre) {
        return repository.selectByEquipe(membre);
    }

    @Override
    public List<Membres> selectByEquipe(Equipes equipe) {
        return repository.selectByEquipe(equipe);
    }

    @Override
    public List<Membres> selectByEquipe(Equipes equipe, Pageable pageable) {
        return repository.selectByEquipe(equipe, pageable);
    }

    @Override
    public List<Membres> recherche(Equipes equipe, String search, Pageable pageable) {
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
    public List<Membres> selectByOrganisation(List<Long> orgs) {
        return repository.selectByOrganisation(orgs);
    }

    @Override
    public List<Membres> selectByOrganisation(List<Long> orgs, Pageable pageable) {
        return repository.selectByOrganisation(orgs, pageable);
    }

    @Override
    public List<Membres> recherche(List<Long> orgs, String search, Pageable pageable) {
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
    public List<Responsables> selectByOrganisationRes(List<Long> orgs) {
        return repository.selectByOrganisationRes(orgs);
    }

    @Override
    public List<Responsables> selectByOrganisationRes(List<Long> orgs, Pageable pageable) {
        return repository.selectByOrganisationRes(orgs, pageable);
    }

    @Override
    public List<Responsables> rechercheRes(List<Long> orgs, String search, Pageable pageable) {
        return repository.rechercheRes(orgs, search, pageable);
    }

    @Override
    public Long countByOrganisationRes(List<Long> orgs) {
        return repository.countByOrganisationRes(orgs);
    }

    @Override
    public Long countRechercheRes(List<Long> orgs, String search) {
        return repository.countRechercheRes(orgs, search);
    }

    @Override
    public void delete(EquipeMembres equipeMembre) {
        repository.delete(equipeMembre);
    }

    @Override
    public void delete(List<EquipeMembres> equipeMembres) {
        repository.deleteAll(equipeMembres);
    }
}
