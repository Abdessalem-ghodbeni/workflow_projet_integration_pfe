package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IEquipeRepository extends JpaRepository<Equipe,Long> {
    Optional<Equipe> findByNom(String nom);
}
