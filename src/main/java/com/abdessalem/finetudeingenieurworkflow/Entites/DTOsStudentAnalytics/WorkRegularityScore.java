package com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkRegularityScore {
    private double consistencyScore;
    private List<String> peakDays;
    private List<String> peakHours;
    private double weekendWorkRatio;
}
