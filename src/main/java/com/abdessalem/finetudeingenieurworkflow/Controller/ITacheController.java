package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;
import com.abdessalem.finetudeingenieurworkflow.Entites.TacheRequest;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITacheServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/tache")
@RequiredArgsConstructor
public class ITacheController {
    private final ITacheServices tacheServices;

    @PostMapping("/ajouter/{etudiantId}")
    public ResponseEntity<ApiResponse> ajouterTache(
                                               @PathVariable("etudiantId") Long etudiantId,
                                               @RequestBody TacheRequest tacheRequest) {

        try {
            ApiResponse response = tacheServices.ajouterTache(tacheRequest,etudiantId);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/modifier/{tacheId}/{etudiantId}")
    public ResponseEntity<ApiResponse> ModifierTache(
            @PathVariable("tacheId") Long tacheId,
            @PathVariable("etudiantId") Long etudiantId,
            @RequestBody TacheRequest tacheRequest) {

        try {
            ApiResponse response = tacheServices.modifierTache(tacheId,tacheRequest,etudiantId);

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
