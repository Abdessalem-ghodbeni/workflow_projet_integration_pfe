package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Justification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IJustificationRepository extends JpaRepository<Justification,Long> {
    List<Justification> findByTacheId(Long tacheId);
}
