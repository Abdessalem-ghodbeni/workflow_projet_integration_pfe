package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.ISujetServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @PostMapping("ajouter")
    public ResponseEntity<Sujet> ajouterSujet(
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("thematique") String thematique,
            @RequestParam("specialite") String specialite,
            @RequestParam(value = "exigences", required = false) List<String> exigences,
            @RequestParam(value = "technologies", required = false) List<String> technologies,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        try {
            // Créer un nouvel objet Sujet
            Sujet sujet = new Sujet();
            sujet.setTitre(titre);
            sujet.setDescription(description);
            sujet.setThematique(thematique);
            sujet.setSpecialite(specialite);

            // Ajouter les exigences et technologies si elles existent
            if (exigences != null) {
                sujet.setExigences(exigences);
            }
            if (technologies != null) {
                sujet.setTechnologies(technologies);
            }




            // Enregistrer le sujet dans la base de données
            Sujet savedSujet = sujetServiceImp.createSujet(sujet);
            return ResponseEntity.ok(savedSujet);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    @PutMapping("modifier-sujet/{id}")
    public ResponseEntity<Sujet> modifierSujet(
            @PathVariable("id") Long id,  // L'ID du sujet à modifier
            @RequestParam(value = "titre",required = false) String titre,
            @RequestParam(value ="description",required = false) String description,
            @RequestParam(value ="thematique",required = false) String thematique,
            @RequestParam(value ="specialite",required = false) String specialite,
            @RequestParam(value = "exigences", required = false) List<String> exigences,
            @RequestParam(value = "technologies", required = false) List<String> technologies

    ) {
        try {
            // Rechercher le sujet existant par ID
            Sujet sujetExist = sujetServiceImp.getSujetById(id);
            if (sujetExist == null) {
                return ResponseEntity.notFound().build();  // Sujet non trouvé
            }

            // Mettre à jour les propriétés du sujet
            sujetExist.setTitre(titre);
            sujetExist.setDescription(description);
            sujetExist.setThematique(thematique);
            sujetExist.setSpecialite(specialite);

            // Mettre à jour les exigences et technologies si elles existent
            if (exigences != null) {
                sujetExist.setExigences(exigences);
            }
            if (technologies != null) {
                sujetExist.setTechnologies(technologies);
            }


            Sujet updatedSujet = sujetServiceImp.updateSujet(sujetExist);
            return ResponseEntity.ok(updatedSujet);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();  // En cas d'erreur
        }
    }







}
