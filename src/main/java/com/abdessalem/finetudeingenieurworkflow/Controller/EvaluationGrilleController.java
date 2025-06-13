package com.abdessalem.finetudeingenieurworkflow.Controller;

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

//Évaluer un étudiant
//    @PostMapping(path = "ajouter")
//    public ResponseEntity<StudentEvaluation> createEvaluation(
//            @RequestBody StudentEvaluationRequest request) {
//        return ResponseEntity.ok(evaluationService.evaluateStudent(request));
//    }

    @PostMapping(path = "ajouter")
    public ResponseEntity<?> createEvaluation(
            @RequestBody StudentEvaluationRequest request) {
        try{
            return new ResponseEntity<>(evaluationService.evaluateStudent(request), HttpStatus.CREATED);

        }catch (Exception exception){
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
}
