package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.SuggestionServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("/suggestion")
@RequiredArgsConstructor
public class SuggestionController {
    private final SuggestionServiceImp suggestionServiceImp;

    @PostMapping("/ajouter/{idTache}/{idActeur}")
    public ResponseEntity<ApiResponse> ajouterSuggestion(
            @PathVariable("idTache") Long idTache, @PathVariable("idActeur") Long idActeur,
            @RequestParam(name = "object",required = false) String object, @RequestParam("contenuTexte") String contenuTexte,
            @RequestParam(required = false) MultipartFile imageFile
    ) {

        try {
            ApiResponse response = suggestionServiceImp.ajouterSuggestion( idTache,  object,  contenuTexte,  imageFile,  idActeur);

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
