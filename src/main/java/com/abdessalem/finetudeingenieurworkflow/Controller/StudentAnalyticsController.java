package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics.CommitImpactProfile;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics.TaskEngagement;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics.WorkRegularityScore;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.StudentAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student-analytics")
@RequiredArgsConstructor
public class StudentAnalyticsController {
    private final StudentAnalyticsService analyticsService;

    @GetMapping("/{etudiantId}/work-regularity")
    public ResponseEntity<WorkRegularityScore> getWorkRegularity(
            @PathVariable Long etudiantId) {
        return ResponseEntity.ok(analyticsService.calculateWorkRegularity(etudiantId));
    }

    @GetMapping("/{etudiantId}/commit-impact")
    public ResponseEntity<CommitImpactProfile> getCommitImpact(
            @PathVariable Long etudiantId) {
        return ResponseEntity.ok(analyticsService.analyzeCommitImpact(etudiantId));
    }

    @GetMapping("/{etudiantId}/task-engagement")
    public ResponseEntity<TaskEngagement> getTaskEngagement(
            @PathVariable Long etudiantId) {
        return ResponseEntity.ok(analyticsService.analyzeTaskEngagement(etudiantId));
    }
}
