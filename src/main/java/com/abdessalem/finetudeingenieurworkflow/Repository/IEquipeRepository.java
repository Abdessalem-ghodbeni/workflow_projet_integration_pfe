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

}
