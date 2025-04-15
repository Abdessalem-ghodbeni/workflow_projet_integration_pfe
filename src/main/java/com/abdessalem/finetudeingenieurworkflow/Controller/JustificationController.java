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
@RequestMapping("/suggestion")
@RequiredArgsConstructor
public class JustificationController {

    private final JustificationServiceImp justificationServiceImp;

    @PostMapping("/ajouter/{idTache}/{idEtudiant}")
    public ResponseEntity<ApiResponse> ajouterTache(
            @PathVariable("idTache") Long idTache, @PathVariable("idEtudiant") Long idEtudiant,
             @RequestParam(name = "objet",required = false) String objet,  @RequestParam("contenuTexte") String contenuTexte,
              @RequestParam(required = false) MultipartFile imageFile
            ) {

        try {
            ApiResponse response = justificationServiceImp.ajouterJustification(idTache,idEtudiant,objet,contenuTexte,imageFile);

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
