package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.IHistoriqueServiceImp;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.ISujetServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.domain.PageRequest;


@RestController
@CrossOrigin("*")
@RequestMapping("/sujet")
@RequiredArgsConstructor
public class SujetController {
    private final ISujetServiceImp sujetServiceImp;
    private final IHistoriqueServiceImp historiqueServiceImp;


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
            @RequestParam("userId") Long userId,
            @RequestParam("description") String description,
            @RequestParam("thematique") String thematique,
            @RequestParam("specialite") String specialite,
            @RequestParam(value = "exigences", required = false) List<String> exigences,
            @RequestParam(value = "technologies", required = false) List<String> technologies,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        try {
            Sujet sujet = new Sujet();
            sujet.setTitre(titre);
            sujet.setDescription(description);
            sujet.setThematique(thematique);
            sujet.setSpecialite(specialite);

            if (exigences != null) {
                sujet.setExigences(exigences);
            }
            if (technologies != null) {
                sujet.setTechnologies(technologies);
            }

            Sujet savedSujet = sujetServiceImp.createSujet(sujet,userId);

            historiqueServiceImp.enregistrerAction(userId, "CREATION",
                    "a ajouté  un sujet dont leur numéro est  " + savedSujet.getId());
            return ResponseEntity.ok(savedSujet);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    @PutMapping("modifier-sujet/{id}")
    public ResponseEntity<Sujet> modifierSujet(
            @PathVariable("id") Long id,
            @RequestParam(value = "titre",required = false) String titre,
            @RequestParam(value ="description",required = false) String description,
            @RequestParam(value ="thematique",required = false) String thematique,
            @RequestParam(value ="specialite",required = false) String specialite,
            @RequestParam(value = "exigences", required = false) List<String> exigences,
            @RequestParam(value = "technologies", required = false) List<String> technologies

    ) {
        try {

            Sujet sujetExist = sujetServiceImp.getSujetById(id);
            if (sujetExist == null) {
                return ResponseEntity.notFound().build();
            }

            sujetExist.setTitre(titre);
            sujetExist.setDescription(description);
            sujetExist.setThematique(thematique);
            sujetExist.setSpecialite(specialite);

            if (exigences != null) {
                sujetExist.setExigences(exigences);
            }
            if (technologies != null) {
                sujetExist.setTechnologies(technologies);
            }
             Sujet updatedSujet = sujetServiceImp.updateSujet(sujetExist);
//            historiqueServiceImp.enregistrerAction(userId, "CREATION",
//                    "a ajouté  un sujet dont leur numéro est  " + savedSujet.getId());
            return ResponseEntity.ok(updatedSujet);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }




    @GetMapping("/list/{tuteurId}")
    public ResponseEntity<Page<Sujet>> getSujetsByTuteurId(
            @PathVariable("tuteurId") Long tuteurId,
            @RequestParam(defaultValue = "0") int page) {
        Page<Sujet> sujets = sujetServiceImp.getSujetsByTuteurId(tuteurId, page);
        return ResponseEntity.ok(sujets);
    }

    @GetMapping("/listSujet/{societeId}")
    public ResponseEntity<Page<Sujet>> getSujetsBySocieteId(
            @PathVariable("societeId") Long societeId,
            @RequestParam(defaultValue = "0") int page) {
        Page<Sujet> sujets = sujetServiceImp.getSujetsBySocietId(societeId, page);
        return ResponseEntity.ok(sujets);
    }


    @GetMapping("/search")
    public Page<Sujet> rechercherSujetParTitre(
            @RequestParam String titre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return sujetServiceImp.rechercherSujetParTitre(titre, pageable);
    }

}
