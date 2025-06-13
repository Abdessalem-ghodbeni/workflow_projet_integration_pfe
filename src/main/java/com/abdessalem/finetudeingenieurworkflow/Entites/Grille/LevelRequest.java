package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;
import lombok.*;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LevelRequest {
    private String levelName;
    private String description;
    private double minScore;
    private double maxScore;
}
