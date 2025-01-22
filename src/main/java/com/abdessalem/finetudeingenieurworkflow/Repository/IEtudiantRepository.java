package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEtudiantRepository extends JpaRepository<Etudiant,Long> {
}
