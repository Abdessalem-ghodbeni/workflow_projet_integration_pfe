package com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics;

import lombok.Builder;

import lombok.Value;

@Builder
@Value // Lombok
public class TransitionMetrics {
   private double avgHours; // Durée moyenne
    private int count;
    public TransitionMetrics(double avgHours, int count) {
        this.avgHours = avgHours;
        this.count = count;
    }
}
