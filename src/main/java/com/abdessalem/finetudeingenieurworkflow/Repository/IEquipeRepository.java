package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Equipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IEquipeRepository extends JpaRepository<Equipe,Long> {
    Optional<Equipe> findByNom(String nom);

    @Query("select distinct e from Equipe e " +
            "join e.etudiants et " +
            "join et.formResponses fr " +
            "where fr.form.id = :formId")
    List<Equipe> findEquipesByFormId(@Param("formId") Long formId);

    @Query("SELECT DISTINCT e FROM Equipe e JOIN e.etudiants et " +
            "WHERE et.specialite = :specialite AND YEAR(e.dateCreation) = YEAR(CURRENT_DATE)")
    List<Equipe> findEquipesBySpecialiteAndCurrentYear(String specialite);

    @Query("SELECT DISTINCT e FROM Equipe e " +
            "JOIN e.etudiants et " +
            "WHERE (YEAR(e.dateCreation) = YEAR(CURRENT_DATE) " +
            "   OR YEAR(e.dateModification) = YEAR(CURRENT_DATE)) " +
            "AND et.specialite = :specialite")
    List<Equipe> findEquipesByYearAndSpecialite(String specialite);


    @Query("SELECT e FROM Etudiant e " +
            "WHERE e.specialite = :specialite " +
            "AND EXTRACT(YEAR FROM e.dateModification) = EXTRACT(YEAR FROM CURRENT_DATE)")
    List<Etudiant> findBySpecialiteAndCurrentYear(@Param("specialite") String specialite);




}
