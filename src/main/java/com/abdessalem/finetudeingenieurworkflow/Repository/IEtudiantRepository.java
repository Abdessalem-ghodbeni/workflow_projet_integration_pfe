package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IEtudiantRepository extends JpaRepository<Etudiant,Long> {
    Optional<Etudiant> findByEmail(String email);
    List<Etudiant> findBySpecialite(String specialite);
}
