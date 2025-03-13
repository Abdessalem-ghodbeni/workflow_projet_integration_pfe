package com.abdessalem.finetudeingenieurworkflow.Entites;

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
public class Equipe implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String image;

    @Enumerated(EnumType.STRING)
    private EtatEquipe etat =EtatEquipe.PENDING;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;
    @UpdateTimestamp
    private LocalDateTime dateModification;


    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL)
    private List<Etudiant> etudiants;
    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatures;
    @OneToMany(mappedBy = "equipe")
    private List<Projet> projets;
}
