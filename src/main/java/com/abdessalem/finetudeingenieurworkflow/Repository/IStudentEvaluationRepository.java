package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Grille.StudentEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IStudentEvaluationRepository extends JpaRepository<StudentEvaluation,Long> {
    @Query("SELECT se FROM StudentEvaluation se " +
            "WHERE YEAR(se.evaluationDate) = YEAR(CURRENT_DATE) " +
            "AND se.evaluationGrid.academicYear = YEAR(CURRENT_DATE)")
    List<StudentEvaluation> findAllForCurrentYear();

    @Query("SELECT se FROM StudentEvaluation se " +
            "WHERE se.student.id = :studentId " +
            "AND YEAR(se.evaluationDate) = YEAR(CURRENT_DATE) " +
            "AND se.evaluationGrid.academicYear = YEAR(CURRENT_DATE)")
    List<StudentEvaluation> findByStudentIdForCurrentYear(@Param("studentId") Long studentId);

    @Query("SELECT se FROM StudentEvaluation se " +
            "LEFT JOIN FETCH se.criterionEvaluations ce " +
            "LEFT JOIN FETCH ce.criterion " +
            "LEFT JOIN FETCH ce.selectedLevel " +
            "WHERE se.id = :evaluationId")
    Optional<StudentEvaluation> findByIdWithDetails(@Param("evaluationId") Long evaluationId);
}
