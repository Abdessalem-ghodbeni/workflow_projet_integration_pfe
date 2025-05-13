package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics.*;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITacheRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.StudentAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student-analytics")
@RequiredArgsConstructor
public class StudentAnalyticsController {
    private final StudentAnalyticsService analyticsService;
    private final ITacheRepository tacheRepository;

    @GetMapping("/{etudiantId}/work-regularity")
    public ResponseEntity<WorkRegularityScore> getWorkRegularity(
            @PathVariable Long etudiantId) {
        return ResponseEntity.ok(analyticsService.calculateWorkRegularity(etudiantId));
    }

//    @GetMapping("/{etudiantId}/commit-impact")
//    public ResponseEntity<CommitImpactProfile> getCommitImpact(
//            @PathVariable Long etudiantId) {
//        return ResponseEntity.ok(analyticsService.analyzeCommitImpact(etudiantId));
//    }
//
//    @GetMapping("/{etudiantId}/task-engagement")
//    public ResponseEntity<TaskEngagement> getTaskEngagement(
//            @PathVariable Long etudiantId) {
//        return ResponseEntity.ok(analyticsService.analyzeTaskEngagement(etudiantId));
//    }
//
//    @GetMapping("/{etudiantId}/advanced-metrics")
//    public ResponseEntity<AdvancedTaskMetrics> getAdvancedMetrics(
//            @PathVariable Long etudiantId) {
//        return ResponseEntity.ok(analyticsService.calculateAdvancedMetrics(etudiantId));
//    }


    @GetMapping("/{etudiantId}/full-report")
    public ResponseEntity<?> getFullStudentReport(
            @PathVariable Long etudiantId, @RequestParam(required = false) Long sprintId) {
       try{
           return ResponseEntity.ok(ResponseEntity.ok(analyticsService.generateAndSaveFullReport(etudiantId,sprintId)));
       }catch (Exception exception){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
       }
    }

    @GetMapping("/evaluate-task/{taskId}")
    public ResponseEntity<?> evaluateTask(@PathVariable Long taskId) {
        try{
            return ResponseEntity.ok(analyticsService.evaluateTask(taskId));
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

    }
}
