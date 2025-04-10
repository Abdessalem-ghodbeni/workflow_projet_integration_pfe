package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Etat;
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

    @GetMapping("/by/{projectId}")

    public ResponseEntity<?> getsprintByIdProjet(@PathVariable Long projectId) {
        try{
            return ResponseEntity.ok(sprintServices.getSprintsByProjetId(projectId));
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

    }


    @DeleteMapping("supprimer/{sprintId}/{etudiantId}")
    public ResponseEntity<ApiResponse> supprimerSprint(@PathVariable Long sprintId,
                                                  @PathVariable Long etudiantId) {
        try {
            ApiResponse response = sprintServices.supprimerSprint(sprintId, etudiantId);
            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }



    @PutMapping(path = "/affecter/tache/to/sprint")
    public ResponseEntity<ApiResponse> AffecterTacheASprint(
            @PathVariable("idTache") Long idTache,
            @PathVariable("idSprint") Long idSprint,
            @PathVariable("idEtudiant") Long idEtudiant
    ) {
        try {
            ApiResponse response = sprintServices.affecterTacheAuSprint(idTache,idSprint,idEtudiant);
            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
               return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getCause().getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
