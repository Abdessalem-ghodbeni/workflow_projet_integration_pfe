package com.abdessalem.finetudeingenieurworkflow.Entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Sprint implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    @Column( columnDefinition = "TEXT")
    private String objectif;
    private Boolean termineDansLesTemps;
    private Integer dureeReelle;
    private LocalDateTime dateDebutPlanifiee;
    private Integer nbTachesCompletees;
    private LocalDateTime dateFinPlanifiee;
    // Date de fin réelle (utile pour comparer à la date planifiée)
    private LocalDateTime dateFinReelle;
    private Double tauxAvancement;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;
    @OneToMany(mappedBy = "sprint")
    private List<Tache> taches;
    @JsonIgnore
    @ManyToOne
    private Projet projet;

}
