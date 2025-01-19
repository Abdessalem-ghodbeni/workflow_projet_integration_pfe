package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Projet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProjetRepository extends JpaRepository<Projet,Long> {
}
