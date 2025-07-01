package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriterionRequest {
    private String name;
    private String description;
    private double maxScore;
    private List<LevelRequest> levels;
}
