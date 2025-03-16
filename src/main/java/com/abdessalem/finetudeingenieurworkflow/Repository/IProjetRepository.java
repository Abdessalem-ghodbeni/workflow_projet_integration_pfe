package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Equipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.Projet;
import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IProjetRepository extends JpaRepository<Projet,Long> {
    boolean existsByEquipeAndSujet(Equipe equipe, Sujet sujet);
    Optional<Projet> findByEquipeAndSujet(Equipe equipe, Sujet sujet);

    @Query("SELECT p FROM Projet p WHERE p.equipe.id = :equipeId")
    Optional<Projet> findByEquipeId(@Param("equipeId") Long equipeId);

}
