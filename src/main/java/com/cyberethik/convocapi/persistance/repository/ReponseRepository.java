package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.*;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ReponseRepository extends JpaRepository<Reponses, Long> {

    @Query("SELECT x FROM Reponses x WHERE x.deleted = false AND x.id=:id")
    Reponses selectById(Long id);

    List<Reponses> findByDeletedFalseOrderByIdDesc();

    List<Reponses> findByDeletedTrueOrderByIdDesc();

    Reponses findTop1ByConvocationAndDeletedFalseOrderByIdDesc(Convocations convocation);
    List<Reponses> findByConvocation(Convocations convocation);
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
    @Query("SELECT x FROM Reponses x WHERE (x.deleted = false AND x.convocation.evenement = :evenement)")
    List<Reponses> selectByEvenement(Evenements evenement);
    @Query("SELECT x FROM Reponses x WHERE (x.deleted = false AND x.convocation.evenement = :evenement)")
    List<Reponses> selectByConvocation(Evenements evenement);

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
    @Query("SELECT DISTINCT x FROM Reponses x WHERE (x.deleted = false AND x.convocation.evenement.id IN :events)")
    List<Reponses> selectByEvenement(List<Long> events, Pageable pageable);

    @Query("SELECT DISTINCT x FROM Reponses x WHERE " +
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

    @Query("SELECT COUNT(DISTINCT x) FROM Reponses x WHERE (x.deleted = false AND x.convocation.evenement.id IN :events)")
    Long countReponses(List<Long> events);

    @Query("SELECT COUNT(DISTINCT x) FROM Reponses x WHERE " +
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

    @Query("SELECT COUNT(x) FROM Reponses x WHERE x.deleted = false AND x.choix = 'Oui, sera présent(e)' AND x.convocation.evenement.organisation.id = :org AND x.convocation.evenement.dateFin > :endDate")
    Long countReponse(Long org, Date endDate);

    @Query("SELECT COUNT(x) FROM Reponses x WHERE (x.deleted = false AND x.choix = 'Oui, sera présent(e)' AND x.convocation.evenement = :evenement)")
    Long countReponsePositif(Evenements evenement);
    @Query("SELECT COUNT(x) FROM Reponses x WHERE (x.deleted = false AND x.choix = 'Non, sera absent(e)' AND x.convocation.evenement = :evenement)")
    Long countReponseNegatif(Evenements evenement);

    @Query("SELECT DISTINCT x.convocation.membre FROM Reponses x WHERE " +
            "(x.convocation.membre.id IN (SELECT em.id.membre.id FROM EquipeMembres em WHERE em.id.equipe.libelle IN :equipes) OR " +
            "x.convocation.membre.libelle IN :membres OR x.dateEnvoi IN :dateReponse OR x.choix IN :reponses) AND (x.deleted = false AND x.convocation.evenement = :evenement)")
    List<Membres> recherche(Evenements evenement, List<String> equipes,
                            List<String> membres,
                            List<Date> dateReponse,
                            List<String> reponses,
                            Pageable pageable);
    @Query("SELECT DISTINCT COUNT(DISTINCT x.convocation.membre) FROM Reponses x WHERE " +
            "(x.convocation.membre.id IN (SELECT em.id.membre.id FROM EquipeMembres em WHERE em.id.equipe.libelle IN :equipes) OR " +
            "x.convocation.membre.libelle IN :membres OR x.dateEnvoi IN :dateReponse OR x.choix IN :reponses) AND (x.deleted = false AND x.convocation.evenement = :evenement)")
    Long countRecherche(Evenements evenement, List<String> equipes, List<String> membres, List<Date> dateReponse, List<String> reponses);
    @Query("SELECT DISTINCT x.convocation.membre FROM Reponses x WHERE " +
            "(x.convocation.membre.id IN (SELECT em.id.membre.id FROM EquipeMembres em WHERE em.id.equipe.libelle IN :equipes) OR " +
            "x.convocation.membre.libelle IN :membres OR x.choix IN :reponses OR x.dateEnvoi BETWEEN :dateReponse1 AND :dateReponse2) AND (x.deleted = false AND x.convocation.evenement = :evenement)")
    List<Membres> recherche(Evenements evenement, List<String> equipes,
                            List<String> membres,
                            Date dateReponse1,
                            Date dateReponse2,
                            List<String> reponses,
                            Pageable pageable);
    @Query("SELECT DISTINCT COUNT(DISTINCT x.convocation.membre) FROM Reponses x WHERE " +
            "(x.convocation.membre.id IN (SELECT em.id.membre.id FROM EquipeMembres em WHERE em.id.equipe.libelle IN :equipes) OR " +
            "x.convocation.membre.libelle IN :membres OR x.choix IN :reponses OR x.dateEnvoi BETWEEN :dateReponse1 AND :dateReponse2) AND (x.deleted = false AND x.convocation.evenement = :evenement)")
    Long countRecherche(Evenements evenement,
                        List<String> equipes,
                        List<String> membres,
                        Date dateReponse1,
                        Date dateReponse2,
                        List<String> reponses);
    @Query("SELECT DISTINCT x.convocation.membre FROM Reponses x WHERE " +
            "(x.convocation.membre.id IN (SELECT em.id.membre.id FROM EquipeMembres em WHERE em.id.equipe.libelle IN :equipes) OR " +
            "x.convocation.membre.libelle IN :membres OR x.choix IN :reponses) AND (x.deleted = false AND x.convocation.evenement = :evenement)")
    List<Membres> recherche(Evenements evenement, List<String> equipes,
                            List<String> membres,
                            List<String> reponses,
                            Pageable pageable);
    @Query("SELECT DISTINCT COUNT(DISTINCT x.convocation.membre) FROM Reponses x WHERE " +
            "(x.convocation.membre.id IN (SELECT em.id.membre.id FROM EquipeMembres em WHERE em.id.equipe.libelle IN :equipes) OR " +
            "x.convocation.membre.libelle IN :membres OR x.choix IN :reponses) AND (x.deleted = false AND x.convocation.evenement = :evenement)")
    Long countRecherche(Evenements evenement,
                        List<String> equipes,
                        List<String> membres,
                        List<String> reponses);

    @Query("SELECT DISTINCT x.convocation.membre FROM Reponses x WHERE (x.deleted = false AND x.convocation.evenement = :evenement)")
    List<Membres> membreEvenement(Evenements evenement);
    @Query("SELECT x FROM Reponses x WHERE (x.deleted = false AND x.convocation.evenement = :evenement AND x.convocation.membre = :membre)")
    List<Reponses> membreEvenement(Evenements evenement, Membres membre);
}
