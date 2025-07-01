package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class EvaluationDetailsDTO {
    private String studentName;
    private String tuteurName;
    private String gridTitle;
    private int academicYear;
    private String option;
    private int gridVersion;
    private LocalDate evaluationDate;
    private double totalScore;
    private List<CriterionEvaluationDetailDTO> criteriaDetails;
}
