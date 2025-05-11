package com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskEngagement {
    private double justificationRatio; // Moyenne de justifications/tâche
    private double suggestionAdoptionRate;
    private double delayedTaskRate; // % de tâches en retard
}
