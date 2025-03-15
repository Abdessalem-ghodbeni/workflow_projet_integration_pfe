package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Equipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.Projet;
import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProjetRepository extends JpaRepository<Projet,Long> {
    boolean existsByEquipeAndSujet(Equipe equipe, Sujet sujet);
}
