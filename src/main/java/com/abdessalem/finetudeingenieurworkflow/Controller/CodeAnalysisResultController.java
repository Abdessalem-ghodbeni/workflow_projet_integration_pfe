package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.CodeAnalysisResult;
import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.CodeAnalysisResultServicesImpl;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.StudentAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/CodeAnalysisResult")
@RequiredArgsConstructor
public class CodeAnalysisResultController {
    private final CodeAnalysisResultServicesImpl codeAnalysisResultServices;



    @PostMapping("/ajouter/{tacheId}/{utilisateurId}")
    public ResponseEntity<ApiResponse> initierAnalyseCode(@PathVariable("tacheId") Long tacheId,
                                               @PathVariable("utilisateurId") Long utilisateurId,
                                               @RequestBody String nomBrancheGit) {

        try {
            ApiResponse response = codeAnalysisResultServices.initierAnalyseCode(tacheId,  nomBrancheGit,utilisateurId);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/modifier-nom-branche/{tacheId}/{utilisateurId}")
    public ResponseEntity<ApiResponse> modifierNomBrancheGit(
            @PathVariable Long tacheId,
            @PathVariable Long utilisateurId,
            @RequestBody String nouveauNomBranche
    ) {
        try {
            ApiResponse response = codeAnalysisResultServices.modifierNomBrancheGitAnalyseActive(tacheId, nouveauNomBranche, utilisateurId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/analyses/taches/{tacheId}/metrics")
    public ResponseEntity<CodeAnalysisResult> getMetrics(
            @RequestParam Long tutorId,
            @PathVariable Long tacheId,
            @RequestParam String branchName
    ) {

        CodeAnalysisResult result = codeAnalysisResultServices
                .analyserEtEnregistrerMetrics(tacheId, branchName, tutorId);
        return ResponseEntity.ok(result);
    }

@GetMapping(path = "liste/analyses/{idTache}")
    public ResponseEntity<?>FetchiliListe(@PathVariable("idTache")Long idTache){
        try {
return ResponseEntity.ok(codeAnalysisResultServices.FetchAllAnalyseByIdTache(idTache));
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
}


}
