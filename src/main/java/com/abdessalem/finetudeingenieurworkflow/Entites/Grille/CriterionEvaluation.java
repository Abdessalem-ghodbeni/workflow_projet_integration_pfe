package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import lombok.*;

import java.io.Serializable;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriterionEvaluation implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private EvaluationCriterion criterion;

    @ManyToOne
    private CriterionLevel selectedLevel;

    private double assignedScore;

    @ManyToOne
    @JoinColumn(name = "student_evaluation_id")
    private StudentEvaluation evaluation;
}
