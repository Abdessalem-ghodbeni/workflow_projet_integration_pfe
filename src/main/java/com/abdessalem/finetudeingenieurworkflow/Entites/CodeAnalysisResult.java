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
public class CodeAnalysisResult implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomBrancheGit;                    // ex: feature/42-auth-admin
    private boolean brancheMergee;                   // true si PR merge dans develop ou main
    private int nombreCommits;                       // nombre total de commits sur la branche
    private int lignesCodeAjoutees;                  // LOC ajoutées
    private int lignesCodeSupprimees;                // LOC supprimées
    private double scoreConsistanceCommits;          // entre 0 et 1 - qualité des messages, cohérence
    private double scoreQualiteCode;                 // Score global de SonarQube par exemple
    private double couvertureTests;                  // % de lignes couvertes par des tests
    private int duplications;                        // Nombre de duplications détectées
    private int bugsDetectes;                        // Nombre de bugs trouvés
    private int codeSmells;                          // Nombre d'odeurs de code
    private boolean estAnalyseActive;                // Pour savoir laquelle est la plus récente/pertinente

    private LocalDateTime dateDerniereAnalyseGit;    // pour savoir quand on a fait la dernière analyse

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tache_id")
    private Tache tache;


}
