package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.CodeAnalysisResult;
import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.CodacyCliService;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.CodeAnalysisResultServicesImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/CodeAnalysisResult")
@RequiredArgsConstructor
public class CodeAnalysisResultController {
    private final CodeAnalysisResultServicesImpl codeAnalysisResultServices;

    private final CodacyCliService analysisService;

    @PostMapping(path = "/let")
    public ResponseEntity<?> analyze(
            @RequestParam String repoUrl,
            @RequestParam String branch) {
        try {
            CodeAnalysisResult result = analysisService.analyzeAndSave(repoUrl, branch);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }
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
        // *Pas* de principal : c'est 100 % user-fourni
        CodeAnalysisResult result = codeAnalysisResultServices
                .analyserEtEnregistrerMetrics(tacheId, branchName, tutorId);
        return ResponseEntity.ok(result);
    }


}
