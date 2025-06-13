package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CriterionEvaluationRequest {
    private Long criterionId;
    private Long selectedLevelId;
    private double assignedScore;
}
