package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EvaluationGrid implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int academicYear;
    private String option;
    @JsonManagedReference
    @OneToMany(mappedBy = "evaluationGrid", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationCriterion> criteria;
    private int version;
    @ManyToOne
    private EvaluationGrid previousVersion;
}
