package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Grille.EvaluationGrid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEvaluationGridRepository extends JpaRepository<EvaluationGrid,Long> {
    List<EvaluationGrid> findByAcademicYearAndOption(int academicYear, String option);
}
