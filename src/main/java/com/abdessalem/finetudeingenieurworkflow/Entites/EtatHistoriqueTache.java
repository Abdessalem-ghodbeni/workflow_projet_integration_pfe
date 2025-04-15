package com.abdessalem.finetudeingenieurworkflow.Entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtatHistoriqueTache implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private EtatTache ancienEtat;
    private EtatTache nouveauEtat;
    private LocalDateTime dateChangement;
    @JsonIgnore
    @ManyToOne
    private User acteur;
    @JsonIgnore
    @ManyToOne
    private Tache tache;
}
