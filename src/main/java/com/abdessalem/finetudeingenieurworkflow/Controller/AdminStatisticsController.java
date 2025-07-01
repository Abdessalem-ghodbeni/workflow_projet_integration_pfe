package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.*;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.StatisticsService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/statistique")
@RequiredArgsConstructor

public class AdminStatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/platform-overview")
    public ResponseEntity<StatisticsDTO> getPlatformStatistics() {
        return ResponseEntity.ok(statisticsService.getPlatformStatistics());
    }

    @PostMapping("/tutor-productivity")
    public ResponseEntity<List<TutorProductivityDTO>> compareTutorProductivity(
            @RequestBody ProductivityComparisonRequest request) {
        return ResponseEntity.ok(statisticsService.compareTutorProductivity(request));
    }

    @GetMapping("/company-subjects")
    public ResponseEntity<Map<String, Long>> getSubjectDistributionByCompany() {
        return ResponseEntity.ok(statisticsService.getSubjectDistributionByCompany());
    }

    @GetMapping("/student-specialties")
    public ResponseEntity<Map<String, Long>> getStudentDistributionBySpecialty() {
        return ResponseEntity.ok(statisticsService.getStudentDistributionBySpecialty());
    }

    @GetMapping("/project-timeline/{tutorId}")
    public ResponseEntity<Map<Integer, Long>> getProjectTimeline(@PathVariable Long tutorId) {
        return ResponseEntity.ok(statisticsService.getProjectTimeline(tutorId));
    }

    // API 1: GET /api/stats/forms
    @GetMapping("/forms")
    public ResponseEntity<List<FormStatsDTO>> getFormsStats() {
        return ResponseEntity.ok(statisticsService.getGlobalFormsStats());
    }

    // API 2: GET /api/stats/tuteurs/{tuteurId} traj3 nombre des formimlairte  w rep par id tuteur

    @GetMapping("/tuteurs/{tuteurId}")
    public ResponseEntity<TuteurFormStatsDTO> getFormsStatsByTuteur(
            @PathVariable Long tuteurId) {
        return ResponseEntity.ok(statisticsService.getFormsStatsByTuteur(tuteurId));
    }
    @GetMapping("/{formId}/responses-count")
    public ResponseEntity<Map<String, Object>> getResponseCount(
            @PathVariable Long formId) {

        Long count = statisticsService.getResponseCountForForm(formId);

        return ResponseEntity.ok(Map.of(
                "formId", formId,
                "responseCount", count
        ));
    }

}
