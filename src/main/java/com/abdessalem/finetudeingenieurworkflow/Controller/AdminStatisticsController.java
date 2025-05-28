package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.ProductivityComparisonRequest;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.StatisticsDTO;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.TutorProductivityDTO;
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
}
