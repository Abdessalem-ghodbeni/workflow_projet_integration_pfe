package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Grille.EvaluationCriterion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEvaluationCriterionRepository extends JpaRepository<EvaluationCriterion,Long> {
}
