package com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnalyticsReport {
    private WorkRegularityScore workRegularity;
    private CommitImpactProfile commitImpact;
    private TaskEngagement taskEngagement;
    private AdvancedTaskMetrics advancedMetrics;
}
