package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentEvaluationRequest {
    private Long studentId;
    private Long tuteurId;
    private Long evaluationGridId;
    private List<CriterionEvaluationRequest> criterionEvaluations;
}
