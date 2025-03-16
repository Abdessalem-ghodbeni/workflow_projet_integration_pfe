package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IEquipeService;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.CandidatureService;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.EquipeServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/equipe")
@RequiredArgsConstructor
public class EquipesControllers {

private final EquipeServiceImp equipeService;

    @PostMapping("/construire/{formId}")
    public ResponseEntity<?> construireEquipes(@PathVariable("formId") Long formId) {
       try {
            ApiResponse response = equipeService.construireEquipes(formId);
            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exception) {
           String errorMessage = (exception.getCause() != null) ? exception.getCause().getMessage() : exception.getMessage();
           return new ResponseEntity<>(new ApiResponse(errorMessage, false), HttpStatus.INTERNAL_SERVER_ERROR);
       }

    }

    @GetMapping(path = "all")
    public ResponseEntity<?> recupererTousEtudiants(){
        List<Equipe> listeEtudiants= equipeService.getAllEquipe();
        return ResponseEntity.ok(listeEtudiants);
    }

    private final CandidatureService candidatureService;
    @GetMapping("/grouped")
    public List<SubjectCandidatureDTO> getCandidaturesGrouped() {
        return candidatureService.getCandidaturesGroupedBySubject();
    }

    @GetMapping("/grouped-with-scores")
    public List<SubjectCandidatureDTO> getCandidaturesWithScores() {
        return candidatureService.getCandidaturesWithScores();
    }


    @GetMapping("/construire_by_form/{formId}")
    public ResponseEntity<List<Equipe>> getEquipesByFormId(@PathVariable Long formId) {
        List<Equipe> equipes = equipeService.recupererListeEquipeByIdFormulaire(formId);
        if (equipes.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si aucune équipe n'est trouvée
        }
        return ResponseEntity.ok(equipes); // Si des équipes sont trouvées
    }



@PutMapping(path = "affecter/etudiant/{etudiantId}/{equipeId}/{idUser}")
    public ResponseEntity<ApiResponse> affcterEtudiantTOEquipe(@PathVariable("etudiantId")Long etudiantId,
                                                               @PathVariable ("equipeId")Long equipeId,
                                                               @PathVariable("idUser")Long idUser)
{
    try {
        ApiResponse response = equipeService.ajouterEtudiantAEquipe(etudiantId,equipeId, idUser);
        if (!response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    } catch (Exception exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getCause().getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
    @PutMapping(path = "retirer/etudiant/{idUser}/{etudiantId}")
    public ResponseEntity<ApiResponse> DesaffcterEtudiantTOEquipe( @PathVariable ("idUser")Long idUser,
    @PathVariable("etudiantId")Long etudiantId) {
        try {
            ApiResponse response = equipeService.retirerEtudiantDeEquipe(idUser,etudiantId);
            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getCause().getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{equipeId}/{tuteurId}/statut")
    public ResponseEntity<ApiResponse> changerStatutEquipe(
            @PathVariable Long equipeId, @PathVariable Long tuteurId,
            @RequestParam EtatEquipe nouveauStatut) {

        try {
            ApiResponse response = equipeService.changerStatutEquipe(equipeId, tuteurId,nouveauStatut);
            return ResponseEntity.ok(response);
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(exception.getMessage(), false));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Erreur interne du serveur.", false));
        }
    }
    @GetMapping("/par-specialite-annee")
    public ResponseEntity<?> getEquipesBySpecialiteAndCurrentYear(@RequestParam String specialite) {
       try{
           return ResponseEntity.ok(equipeService.getEquipesByYearAndSpecialite(specialite));
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
       }
    }


    @GetMapping("/par-etudiant/{etudiantId}")
    public ResponseEntity<?> getEquipeByEtudiantId(@PathVariable Long etudiantId) {
        try{


        Equipe equipe = equipeService.getEquipeByEtudiantId(etudiantId);

        if (equipe != null) {
            return ResponseEntity.ok(equipe);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucune équipe trouvée pour l'étudiant avec l'ID : " + etudiantId);
        }
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @PostMapping("/by-ids")
    public ResponseEntity<?> getEquipesByIds(@RequestBody List<Long> ids) {
       try{
           return new ResponseEntity<>(equipeService.getEquipesByIds(ids),HttpStatus.OK);

       }catch (Exception exception){
           return new ResponseEntity<>(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/isEquipeAssignedToSujet/{equipeId}")
    public ResponseEntity<Map<String, Object>> isEquipeeAssignedToSujet(@PathVariable Long equipeId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Projet> projet = equipeService.getProjetByEquipeId(equipeId);

        if (projet.isPresent()) {
            response.put("assigned", true);
            response.put("subjectTitle", projet.get().getSujet().getTitre());
        } else {
            response.put("assigned", false);
        }

        return ResponseEntity.ok(response);
    }



}
