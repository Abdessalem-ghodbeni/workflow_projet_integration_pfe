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
// Dans CodeAnalysisResult.java
// Ajouter ces nouveaux champs :

    @Column(name = "average_hours_between_commits")
    private Double averageHoursBetweenCommits;

    @Column(name = "branch_lifespan_days")
    private Long branchLifespanDays;

    @Column(name = "merge_conflict_detected")
    private Boolean mergeConflictDetected;

    @Column(name = "average_additions_per_commit")
    private Double averageAdditionsPerCommit;

    @Column(name = "average_deletions_per_commit")
    private Double averageDeletionsPerCommit;

    @Column(name = "commit_types_distribution", columnDefinition = "TEXT")
    private String commitTypesDistribution;

    @Column(name = "score_qualite_commit_message")
    private Double scoreQualiteCommitMessage;

    @Column(name = "heure_travail_distribution", columnDefinition = "TEXT")
    private String heureTravailDistribution;

    @Column(name = "jour_travail_distribution", columnDefinition = "TEXT")
    private String jourTravailDistribution;

    @Column(name = "pattern_travail")
    private String patternTravail;





    @Column(name = "security_vulnerabilities")
    private Integer securityVulnerabilities;



    @Column(name = "critical_bugs")
    private Integer criticalBugs;



    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tache_id")
    private Tache tache;


}
