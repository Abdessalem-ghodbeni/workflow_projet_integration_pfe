package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.CodeAnalysisResultServicesImpl;
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


    @PostMapping("/{tacheId}/{utilisateurId}")
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




}
