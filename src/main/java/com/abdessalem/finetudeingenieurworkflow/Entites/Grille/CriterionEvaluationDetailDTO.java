package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CriterionEvaluationDetailDTO {
    private String criterionName;
    private String description;
    private double maxScore;
    private double assignedScore;
    private String selectedLevel;
}
