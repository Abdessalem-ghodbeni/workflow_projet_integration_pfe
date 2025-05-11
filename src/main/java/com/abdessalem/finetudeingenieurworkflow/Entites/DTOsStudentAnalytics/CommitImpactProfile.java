package com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommitImpactProfile {
    private double avgCommitSize; // LOC moyen par commit
    private double mergeConflictRate; // Taux de branches avec conflits
    private double branchLongevity; // Durée moyenne de vie des branches
}
