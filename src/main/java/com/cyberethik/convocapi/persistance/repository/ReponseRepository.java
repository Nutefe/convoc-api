package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReponseRepository extends JpaRepository<Reponses, Long> {

    @Query("SELECT x FROM Reponses x WHERE x.deleted = false AND x.id=:id")
    Reponses selectById(Long id);

    List<Reponses> findByDeletedFalseOrderByIdDesc();

    List<Reponses> findByDeletedTrueOrderByIdDesc();

    List<Reponses> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    @Query("SELECT x FROM Reponses x WHERE " +
            "(x.choix LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.description LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    List<Reponses> recherche(String search, Pageable pageable);

    @Query("SELECT COUNT(x) FROM Reponses x WHERE x.deleted = false")
    Long countReponses();

    @Query("SELECT COUNT(x) FROM Reponses x WHERE " +
            "(x.choix LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.description LIKE CONCAT('%',:search,'%')) AND x.deleted = false")
    Long countRecherche(String search);

    @Query("SELECT x FROM Reponses x WHERE (x.deleted = false AND x.convocation.evenement = :evenement)")
    List<Reponses> selectByEvenement(Evenements evenement, Pageable pageable);

    @Query("SELECT x FROM Reponses x WHERE " +
            "(x.choix LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.description LIKE CONCAT('%',:search,'%')) AND (x.deleted = false AND x.convocation.evenement = :evenement)")
    List<Reponses> recherche(Evenements evenement, String search, Pageable pageable);

    @Query("SELECT COUNT(x) FROM Reponses x WHERE (x.deleted = false AND x.convocation.evenement = :evenement)")
    Long countReponses(Evenements evenement);

    @Query("SELECT COUNT(x) FROM Reponses x WHERE " +
            "(x.choix LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.description LIKE CONCAT('%',:search,'%')) AND (x.deleted = false AND x.convocation.evenement = :evenement)")
    Long countRecherche(Evenements evenement, String search);
    @Query("SELECT x FROM Reponses x WHERE (x.deleted = false AND x.convocation.evenement.id IN :events)")
    List<Reponses> selectByEvenement(List<Long> events, Pageable pageable);

    @Query("SELECT x FROM Reponses x WHERE " +
            "(x.choix LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.description LIKE CONCAT('%',:search,'%')) AND (x.deleted = false AND x.convocation.evenement.id IN :events)")
    List<Reponses> recherche(List<Long> events, String search, Pageable pageable);

    @Query("SELECT COUNT(x) FROM Reponses x WHERE (x.deleted = false AND x.convocation.evenement.id IN :events)")
    Long countReponses(List<Long> events);

    @Query("SELECT COUNT(x) FROM Reponses x WHERE " +
            "(x.choix LIKE CONCAT('%',:search,'%') OR x.description LIKE CONCAT('%',:search,'%') OR " +
            "x.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.dateEnvoi LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.email LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.libelle LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.telephone LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.membre.responsable.adresse LIKE CONCAT('%',:search,'%') OR " +
            "x.convocation.evenement.description LIKE CONCAT('%',:search,'%')) AND (x.deleted = false AND x.convocation.evenement.id IN :events)")
    Long countRecherche(List<Long> events, String search);
    
}
