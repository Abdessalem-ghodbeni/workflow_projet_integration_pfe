package com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique;

import java.util.List;

public record ProductivityComparisonRequest(
        List<Long> tutorIds,
        Integer year,
        String comparisonMetric
) {
}
