package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.TacheRequest;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.JustificationServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("/justification")
@RequiredArgsConstructor
public class JustificationController {

    private final JustificationServiceImp justificationServiceImp;

    @PostMapping("/ajouter/{idTache}/{idEtudiant}")
    public ResponseEntity<ApiResponse> ajouterJustification(
            @PathVariable("idTache") Long idTache, @PathVariable("idEtudiant") Long idEtudiant,
             @RequestParam(name = "object",required = false) String object,  @RequestParam("contenuTexte") String contenuTexte,
              @RequestParam(required = false) MultipartFile imageFile
            ) {

        try {
            ApiResponse response = justificationServiceImp.ajouterJustification(idTache,idEtudiant,object,contenuTexte,imageFile);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PutMapping("/modifier/{idJustification}/{idEtudiant}")
    public ResponseEntity<ApiResponse> ModifierJustification(
            @PathVariable("idJustification") Long idTache, @PathVariable("idEtudiant") Long idEtudiant,
            @RequestParam(name = "nouvelObjet",required = false) String nouvelObjet,  @RequestParam("nouveauContenuTexte") String nouveauContenuTexte,
            @RequestParam(required = false) MultipartFile nouvelleImage
    ) {

        try {
            ApiResponse response = justificationServiceImp.ajouterJustification(idTache,idEtudiant,nouvelObjet,nouveauContenuTexte,nouvelleImage);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{JustificationId}")
    public ResponseEntity<?> getJustificationById(@PathVariable("JustificationId") Long JustificationId) {
        try {
            return ResponseEntity.ok(justificationServiceImp.getJustificationById(JustificationId));

        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

    }


    @GetMapping("/byTache/{tacheId}")
    public ResponseEntity<?> getJustificationByTacheId(@PathVariable("tacheId") Long tacheId) {
        try {
            return ResponseEntity.ok(justificationServiceImp.findByTacheId(tacheId));

        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

    }

}
