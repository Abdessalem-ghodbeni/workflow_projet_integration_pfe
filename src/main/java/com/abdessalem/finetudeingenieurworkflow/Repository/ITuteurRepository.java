package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface ITuteurRepository extends JpaRepository<Tuteur,Long> {
    Page<Tuteur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);
}
