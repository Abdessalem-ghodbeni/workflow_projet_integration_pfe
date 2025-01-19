package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.ISujetServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/sujet")
@RequiredArgsConstructor
public class SujetController {
    private final ISujetServiceImp sujetServiceImp;
//    @PostMapping("ajouter/{utilisateurId}")
@PutMapping("/update")
public ResponseEntity<?> updateSujet( @RequestBody Sujet sujet) {
    try {
        Sujet updatedSujet = sujetServiceImp.updateSujet(sujet);
        return ResponseEntity.status(HttpStatus.OK).body(updatedSujet);
    } catch (RessourceNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur interne est survenue. Veuillez réessayer.");
    }
}

    @PostMapping("ajouter")
public ResponseEntity<?> createSujet(
//        @PathVariable Long utilisateurId,
        @RequestBody Sujet sujet) {
    try {
        Sujet nouveauSujet = sujetServiceImp.createSujet(sujet);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauSujet);
    } catch (RessourceNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Une erreur interne est survenue. Veuillez réessayer.");
    }
}
    @GetMapping("/all")
    public ResponseEntity<?> getAllSujets() {
        try {
            List<Sujet> sujets = sujetServiceImp.getAllSujets();
            return ResponseEntity.status(HttpStatus.OK).body(sujets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne est survenue. Veuillez réessayer.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSujet(@PathVariable Long id) {
        try {
            sujetServiceImp.deleteSujet(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Sujet supprimé avec succès.");
        } catch (RessourceNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne est survenue. Veuillez réessayer.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSujetById(@PathVariable Long id) {
        try {
            Sujet sujet = sujetServiceImp.getSujetById(id);
            return ResponseEntity.status(HttpStatus.OK).body(sujet);
        } catch (RessourceNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne est survenue. Veuillez réessayer.");
        }
    }



}
