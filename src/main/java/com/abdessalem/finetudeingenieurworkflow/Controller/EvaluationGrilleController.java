package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;
import com.abdessalem.finetudeingenieurworkflow.Entites.Grille.*;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/grille/evaluations")
@RequiredArgsConstructor
public class EvaluationGrilleController {
    private final EvaluationService evaluationService;


    @PostMapping(path = "ajouter")
    public ResponseEntity<ApiResponse> createEvaluation( @RequestBody StudentEvaluationRequest request) {

        try {
            ApiResponse response = evaluationService.evaluateStudent(request);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//Obtenir des suggestions de niveaux pour un score
    @GetMapping("/suggestions")
    public ResponseEntity<List<CriterionLevel>> getSuggestions(
            @RequestParam Long criterionId,
            @RequestParam double score) {
        return ResponseEntity.ok(evaluationService.suggestLevels(criterionId, score));
    }


//    Créer une nouvelle grille d'évaluation
    @PostMapping("/grids")
    public ResponseEntity<EvaluationGrid> createGrid(
            @RequestBody EvaluationGridRequest request) {
        return ResponseEntity.ok(evaluationService.createEvaluationGrid(request));
    }


    //liste evaluation created by chef d'option
    @GetMapping(path = "evaluation/listes")
    public ResponseEntity<List<EvaluationGrid>> getEvaluationGrids(
            @RequestParam("academicYear") int academicYear,
            @RequestParam("option") String option) {

        List<EvaluationGrid> grids = evaluationService.getEvaluationGridsByYearAndOption(academicYear, option);

        if(grids.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(grids);
    }

    @GetMapping("/by-id/{idGrille}")
    public ResponseEntity<?> getSuggestions(
            @PathVariable("idGrille") Long idGrille) {
       try{
           return ResponseEntity.ok(evaluationService.getEvaluationGridById(idGrille));

       }catch (Exception exception){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
       }
    }


    @GetMapping(path = "grilleEtudiant")
    public ResponseEntity<?> getAllEvaluationsForCurrentYear() {
      try{
          return ResponseEntity.ok(evaluationService.getAllEvaluationsForCurrentYear());
      }catch (Exception exception){
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
      }
    }

    @GetMapping("/evaluated/student/{studentId}")
    public ResponseEntity<?> getStudentEvaluationsForCurrentYear(
            @PathVariable Long studentId) {
        try{
            return ResponseEntity.ok(evaluationService.getStudentEvaluationsForCurrentYear(studentId));
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @GetMapping("/{evaluationId}/details")
    public ResponseEntity<?> getEvaluationDetails(
            @PathVariable Long evaluationId) {
      try{
          return ResponseEntity.ok(evaluationService.getEvaluationDetails(evaluationId));

      }catch (Exception exception){
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
      }
    }


}
