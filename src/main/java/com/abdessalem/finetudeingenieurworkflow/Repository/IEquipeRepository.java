package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Equipe;
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


}
