package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;

import java.io.Serializable;

import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEvaluation implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Etudiant student;

    @ManyToOne
    private Tuteur tuteur;

    @ManyToOne
    private EvaluationGrid evaluationGrid;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CriterionEvaluation> criterionEvaluations;

    private double totalScore;
    private LocalDate evaluationDate;
}
