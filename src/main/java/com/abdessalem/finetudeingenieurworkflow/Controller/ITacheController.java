package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITacheServices;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.ITacheServiceImp;
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

    @GetMapping("/{tacheId}")
    public ResponseEntity<?> getTacheById(@PathVariable Long tacheId) {
        try{
            return ResponseEntity.ok(tacheServices.getTacheById(tacheId));

        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

    }


    @DeleteMapping("supprimer/{tacheId}/{etudiantId}")
    public ResponseEntity<ApiResponse> deleteEpic(@PathVariable Long tacheId,
                                                  @PathVariable Long etudiantId) {
         try {
            ApiResponse response = tacheServices.supprimerTache(tacheId, etudiantId);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
    @GetMapping("/backlog/{projectId}")
//    n3adi fi id project bec hn'accedi bih lil backlog associé lih '
    public ResponseEntity<?> getTacheByIdBacklog(@PathVariable Long projectId) {
        try{
            return ResponseEntity.ok(tacheServices.getTachesByProjetId(projectId));

        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

    }

    @PutMapping("/affecter/{tacheId}/{idEtudiantCible}/{idUserQuiAffecte}")
    public ResponseEntity<ApiResponse> affecterTacheAEtudiant(
            @PathVariable("tacheId") Long tacheId,
            @PathVariable("idEtudiantCible") Long idEtudiantCible,
            @PathVariable("idUserQuiAffecte") Long idUserQuiAffecte) {

        try {
            ApiResponse response = tacheServices.affecterTacheAEtudiant(tacheId,idEtudiantCible,idUserQuiAffecte);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Dans un @RestController temporaire
    @GetMapping("/test-scheduler")
    public void testScheduler() {
        tacheServices.checkOverdueTasks();
    }

    @PutMapping("/kanban/etat")
    public ResponseEntity<ApiResponse> ChangeEtatTache(

            @RequestParam("tacheId") Long tacheId,
            @RequestParam("idEtudiant") Long idEtudiant,
            @RequestParam EtatTache etatTache
    ) {

        try {
            ApiResponse response = tacheServices.changerEtatTache(tacheId,etatTache,idEtudiant);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/calendar/{etudiantId}")
    public ResponseEntity<?> getTachesForCalendar(
            @PathVariable Long etudiantId) {

    try{
        return ResponseEntity.ok(tacheServices.getTachesForEtudiant(etudiantId));

    }catch (Exception exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
    }



}
