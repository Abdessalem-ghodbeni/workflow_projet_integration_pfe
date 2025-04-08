package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Sprint;

import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ISprintServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/sprint")
@RequiredArgsConstructor
public class SprintController {
    private final ISprintServices sprintServices;

    @PostMapping("/ajouter/{projetId}/{etudiantId}")
    public ResponseEntity<ApiResponse> ajouterSprint(
            @PathVariable("etudiantId") Long etudiantId,
            @PathVariable("projetId") Long projetId,
            @RequestBody Sprint request) {

        try {
            ApiResponse response = sprintServices.ajouterSprint(request,projetId,etudiantId);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/modifier/{sprintId}/{etudiantId}")
    public ResponseEntity<ApiResponse> ModifierSprint(
            @PathVariable("sprintId") Long sprintId,
            @PathVariable("etudiantId") Long etudiantId,
            @RequestBody Sprint SprintRequest) {

        try {
            ApiResponse response = sprintServices.modifierSprint(sprintId,SprintRequest,etudiantId);

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
