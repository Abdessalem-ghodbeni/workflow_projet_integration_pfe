package com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
@Data
@Builder
public class AdvancedTaskMetrics {
    private double lowCommitPenalty; // 0-1 (0 = pas de pénalité)
    private boolean potentialPlagiarismRisk;
    private double blockRate; // % tâches bloquées
    private boolean frequentBlockingIssue;
    private Map<String, Double> stateTransitionTimes; // Moyenne en heures entre états

    private Map<String, TransitionMetrics> stateTransitions;
    private double stagnationPenalty;
    private double reactivityScore;



}
