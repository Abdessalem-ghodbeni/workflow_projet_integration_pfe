package com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EnhancedEvaluationResult {
    private CodeHealthMetrics codeHealth;

    // Comportement de développement
    private DevelopmentBehavior behavior;

    // Efficacité collaborative
    private CollaborationMetrics collaboration;

    // Progression temporelle
    private TemporalAnalysis progression;

    // Scores composites
    private CompositeScores scores;

    // Recommandations générées
    private List<String> recommendations;

    // Sous-classes détaillées
    @Data
    @Builder
    public static class CodeHealthMetrics {
        private double codeStabilityIndex; // (Additions/Deletions) ratio
        private int criticalIssuesDensity; // Bugs + Vulnérabilités
        private double codeSustainability; // (Lignes utiles)/(Lignes totales)
        private double testCoverageTrend; // Évolution sur les analyses
    }

    @Data
    @Builder
    public static class DevelopmentBehavior {
        private double focusFactor; // Nombre de commits ciblés
        private double workflowConsistency; // Écart-type des heures de commit
        private double refactoringRatio; // Commits de refactoring / total
        private double crisisIndicator; // Commits urgents (nuit/weekend)
    }

    @Data
    @Builder
    public static class CollaborationMetrics {
        private double mergeEfficiency; // Temps avant merge réussi
        private double conflictResolutionSkill; // Conflits résolus / total
        private double reviewResponsiveness; // Temps réponse aux commentaires
        private double documentationQuality; // Ratio docs/modifs
    }

    @Data
    @Builder
    public static class TemporalAnalysis {
        private double learningCurve; // Amélioration métriques clés sur temps
        private double consistencyScore; // Régularité des contributions
        private List<String> phaseTransitions; // Ex: "Prototype→Production"
        private double recoveryCapacity; // Capacité à corriger les regressions
    }

    @Data
    @Builder
    public static class CompositeScores {
        private double technicalMastery;
        private double teamContribution;
        private double projectOwnership;
        private double agileMaturity;
    }
}
