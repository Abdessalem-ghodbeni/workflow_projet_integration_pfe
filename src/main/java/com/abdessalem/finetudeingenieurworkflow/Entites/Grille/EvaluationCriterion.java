package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonIgnoreProperties({"evaluationGrid", "levels"})
public class EvaluationCriterion implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double maxScore;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "grid_id")
    private EvaluationGrid evaluationGrid;

    @OneToMany(mappedBy = "criterion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CriterionLevel> levels;
}
