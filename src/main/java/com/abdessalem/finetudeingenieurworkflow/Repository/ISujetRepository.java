package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISujetRepository extends JpaRepository<Sujet,Long> {
    Page<Sujet> findByTuteurId(Long tuteurId, Pageable pageable);
    List<Sujet> findByTitreContainingIgnoreCase(String titre);

    Page<Sujet> findByTitreContainingIgnoreCase(String titre, Pageable pageable);
}
