package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Appreciation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IAppreciationRepository extends JpaRepository<Appreciation,Long> {
    List<Appreciation> findByEtudiantIdOrderByEvaluationDateDesc(Long etudiantId);
}
