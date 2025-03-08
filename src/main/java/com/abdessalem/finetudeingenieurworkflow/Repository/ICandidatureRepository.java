package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Candidature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICandidatureRepository extends JpaRepository<Candidature,Long> {
}
