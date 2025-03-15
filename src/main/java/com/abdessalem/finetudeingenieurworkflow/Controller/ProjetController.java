package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Projet;
import com.abdessalem.finetudeingenieurworkflow.Entites.ProjetRequest;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.ProjetServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/projet")
@RequiredArgsConstructor
public class ProjetController {
    private final ProjetServiceImp projetServiceImp;

    @PostMapping
    public ResponseEntity<?> ajouterProjet(@RequestBody ProjetRequest projetRequest) {
        Projet savedProjet = projetServiceImp.ajouterProjet(projetRequest);
        return ResponseEntity.ok(savedProjet);
    }


    @PostMapping(path = "affecter/equipe/sujet/{equipeId}/{tuteurId}")
    public ResponseEntity<ApiResponse> affcterSujetTOEquipe(@PathVariable("equipeId")Long equipeId,
                                                               @PathVariable ("tuteurId")Long tuteurId,
                                                               @RequestBody String titreSujet)
    {
        try {
            ApiResponse response = projetServiceImp.affecterSujetAEquipe(titreSujet,equipeId, tuteurId);
            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getCause().getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping(path = "retirer/equipe/sujet/{equipeId}/{tuteurId}")
    public ResponseEntity<ApiResponse> DesaffcterSujetTOEquipe(@PathVariable("equipeId")Long equipeId,
                                                            @PathVariable ("tuteurId")Long tuteurId,
                                                            @RequestBody String titreSujet)
    {
        try {
            ApiResponse response = projetServiceImp.desaffecterSujetAEquipe(titreSujet,equipeId, tuteurId);
            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getCause().getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
