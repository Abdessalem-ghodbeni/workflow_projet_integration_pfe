package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Grille.StudentEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IStudentEvaluationRepository extends JpaRepository<StudentEvaluation,Long> {
}
