package com.cyberethik.convocapi.persistance.service.impl;

import com.cyberethik.convocapi.persistance.entities.Files;
import com.cyberethik.convocapi.persistance.repository.FileRepository;
import com.cyberethik.convocapi.persistance.service.dao.FileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FileImp implements FileDao {

    @Autowired
    private FileRepository repository;

    @Override
    public Optional<Files> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Files save(Files file) {
        return repository.save(file);
    }

    @Override
    public Files selectById(Long id) {
        return repository.selectById(id);
    }

    @Override
    public List<Files> findByDeletedFalseOrderByIdDesc() {
        return repository.findByDeletedFalseOrderByIdDesc();
    }

    @Override
    public List<Files> findByDeletedTrueOrderByIdDesc() {
        return repository.findByDeletedTrueOrderByIdDesc();
    }

    @Override
    public List<Files> findByDeletedFalseOrderByIdDesc(Pageable pageable) {
        return repository.findByDeletedFalseOrderByIdDesc(pageable);
    }

    @Override
    public List<Files> recherche(String search, Pageable pageable) {
        return repository.recherche(search, pageable);
    }

    @Override
    public Long counts() {
        return repository.count();
    }

    @Override
    public Long countFiles() {
        return repository.countFiles();
    }

    @Override
    public Long countRecherche(String search) {
        return repository.countRecherche(search);
    }
}
