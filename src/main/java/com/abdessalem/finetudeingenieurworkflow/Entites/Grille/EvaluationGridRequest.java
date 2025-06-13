package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationGridRequest {
    private String title;
    private int academicYear;
    private String option;
    private List<CriterionRequest> criteria;
}
