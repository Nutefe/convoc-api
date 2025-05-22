package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Roles;
import com.cyberethik.convocapi.persistance.repository.RoleRepository;
import com.cyberethik.convocapi.persistance.service.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleImp implements RoleDao {

    @Autowired
    private RoleRepository repository;

    @Override
    public Optional<Roles> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Roles save(Roles role) {
        return repository.save(role);
    }

    @Override
    public Roles selectById(Integer id) {
        return repository.selectById(id);
    }

    @Override
    public List<Roles> findByDeletedFalseOrderByIdDesc() {
        return repository.findByDeletedFalseOrderByIdDesc();
    }

    @Override
    public List<Roles> findByDeletedTrueOrderByIdDesc() {
        return repository.findByDeletedTrueOrderByIdDesc();
    }

    @Override
    public List<Roles> findByDeletedFalseOrderByIdDesc(Pageable pageable) {
        return repository.findByDeletedFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<Roles> recherche(String search, Pageable pageable) {
        return repository.recherche(search, pageable);
    }

    @Override
    public Long countRoles() {
        return repository.countRoles();
    }

    @Override
    public Long count() {
        return repository.count();
    }

    @Override
    public Long countRecherche(String search) {
        return repository.countRecherche(search);
    }

    @Override
    public void delete(Roles role) {
        repository.delete(role);
    }

//    @Override
//    public boolean rolePro(Pros pro) {
////        Pros proInit = this.proRepository.selectById(pro.getId());
//        List<ProModules> proModules = this.proModuleRepository.selectByPro(pro);
//        return repository.;
//    }
}
