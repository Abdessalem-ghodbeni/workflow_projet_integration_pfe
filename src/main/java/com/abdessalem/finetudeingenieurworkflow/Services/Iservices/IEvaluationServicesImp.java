package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.Grille.*;

import java.util.List;

public interface IEvaluationServicesImp {
    StudentEvaluation evaluateStudent(StudentEvaluationRequest request);
    List<CriterionLevel> suggestLevels(Long criterionId, double score);
    EvaluationGrid createEvaluationGrid(EvaluationGridRequest request);
    List<EvaluationGrid> getEvaluationGridsByYearAndOption(int academicYear, String option);
}
