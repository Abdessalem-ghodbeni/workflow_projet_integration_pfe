package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriterionLevel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String levelName; // A, B, C, D
    private String description;
    private double minScore;
    private double maxScore;

    @ManyToOne
    @JoinColumn(name = "criterion_id")
    @JsonIgnore
    private EvaluationCriterion criterion;
}
