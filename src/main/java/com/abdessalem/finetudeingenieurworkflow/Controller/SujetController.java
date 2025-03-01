package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.IHistoriqueServiceImp;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.ISujetServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> getAllSujets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("dateModification").descending());
            Page<Sujet> sujets = sujetServiceImp.getAllSujets(pageable);
            return ResponseEntity.ok(sujets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
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
            sujet.setEtat(Etat.ENCOURS);

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

    @GetMapping("/created-by-societe")
    public Page<Sujet> getSujetsCreatedBySociete(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return sujetServiceImp.getSujetsCreatedBySociete(PageRequest.of(page, size));
    }

    @GetMapping(path = "/liste/filter")
    public ResponseEntity<?> getFilters() {
        try{
            FilterDTO filterDTO= sujetServiceImp.getFilters();
            return ResponseEntity.ok(filterDTO);
        }
        catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());

        }
    }


    @PutMapping(path = "/change/etat")
    public ResponseEntity<ApiResponse> changerEtatSujet(
            @RequestParam(value = "idUser") Long idUser,
            @RequestParam(value = "idSujet") Long idSujet,
            @RequestParam(value = "nouvelEtat") Etat nouvelEtat
           ) {
        try {
            ApiResponse response = sujetServiceImp.changerEtatSujet(idSujet,nouvelEtat);
            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                historiqueServiceImp.enregistrerAction(idUser, "Modification", "Changement du d'etat sujet en "+nouvelEtat);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getCause().getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @GetMapping("/filtered/by/criteres")
    public Page<Sujet> getFilteredSujets(
            @RequestParam(required = false) List<String> thematiques,
            @RequestParam(required = false) List<Integer> annees,
            @RequestParam(required = false) List<String> societes,
            @RequestParam(required = false) List<String> specialites,
            @RequestParam(required = false) List<Etat> etats,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        System.out.println("Thematiques: " + thematiques);
        System.out.println("Annees: " + annees);
        System.out.println("Societes: " + societes);
        System.out.println("Specialites: " + specialites);
        System.out.println("Etats: " + etats);
        System.out.println("Page: " + page + ", Size: " + size);
        List<String> normalizedSpecialites = specialites != null
                ? specialites.stream().map(String::toLowerCase).collect(Collectors.toList())
                : null;
        return sujetServiceImp.getSujetsByFilters(thematiques, annees, societes, normalizedSpecialites, etats, PageRequest.of(page, size));
    }



    @GetMapping(path = "/initialiser/by/tuteur")
    public ResponseEntity<Page<Sujet>> getAllSujetsCreatedByTuteurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Sujet> sujets = sujetServiceImp.listSujetsCreatedByTureurs(pageable);
        return ResponseEntity.ok(sujets);
    }

    @GetMapping("/filters/suj/tuteurs")
    public ResponseEntity<?> getFilterCriteria() {
    try{
        FilterTutorDTO filterCriteria = sujetServiceImp.getFilterCriteria();
        return ResponseEntity.ok(filterCriteria);
    }catch (Exception exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    }


    @GetMapping("/tuteur_subject_made/filter")
    public ResponseEntity<Page<Sujet>> filterSujets(
            @RequestParam(value = "thematiques", required = false) List<String> thematiques,
            @RequestParam(value = "annees", required = false) List<Integer> annees,
            @RequestParam(value = "titres", required = false) List<String> titres,
            @RequestParam(value = "tuteurs", required = false) List<String> tuteurs,
            @RequestParam(value = "specialites", required = false) List<String> specialites,
            @RequestParam(value = "etats", required = false) List<String> etatsString,
            Pageable pageable) {


        List<com.abdessalem.finetudeingenieurworkflow.Entites.Etat> etats = null;
        if (etatsString != null && !etatsString.isEmpty()) {
            etats = etatsString.stream()
                    .map(com.abdessalem.finetudeingenieurworkflow.Entites.Etat::valueOf)
                    .toList();
        }
        List<String> normalizedSpecialites = specialites != null
                ? specialites.stream().map(String::toLowerCase).collect(Collectors.toList())
                : null;

        Page<Sujet> sujets = sujetServiceImp.filterSujetsCreatedByAllTuteurs(thematiques, annees, titres, tuteurs, normalizedSpecialites, etats, pageable);
        return ResponseEntity.ok(sujets);
    }


    @GetMapping("/accepteed")
    public ResponseEntity<?> getSujetsAccepteed(Pageable pageable) {
     try{
       return ResponseEntity.ok(sujetServiceImp.getSujetsAccepteed(pageable));
     }catch (Exception ex){
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
     }
    }




    @GetMapping("/accepteed/filters")
    public ResponseEntity<?> getAllFiltersForAcceptedSujets() {
        try{
            SujetAcceptedFiltersDTO filterCriteria = sujetServiceImp.getAllFiltersForAcceptedSujets();
            return ResponseEntity.ok(filterCriteria);
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

    }



    @GetMapping("/accepteed/and_filter")
    public ResponseEntity<?> listeAcceptedSujetsFiltred(
            @RequestParam(required = false) List<String> thematiques,
            @RequestParam(required = false) List<String> specialites,
            @RequestParam(required = false) List<Integer> annees,
            @RequestParam(required = false) List<String> titres,
            Pageable pageable
    ) {
        try{
            List<String> normalizedSpecialites = specialites != null
                    ? specialites.stream().map(String::toLowerCase).collect(Collectors.toList())
                    : null;

            return ResponseEntity.ok(sujetServiceImp.filterAcceptedSujets(thematiques, normalizedSpecialites, annees, titres, pageable));
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @PutMapping("/{tuteurId}/sujets/visibles")
    public ResponseEntity<?> rendreSujetsVisibles(@PathVariable Long tuteurId, @RequestBody List<Long> sujetIds) {
        try {

            historiqueServiceImp.enregistrerAction(tuteurId, "Modification",
                    "a partagé les sujets avec les etudiants "  );
            return ResponseEntity.ok(sujetServiceImp.rendreSujetsVisibles(tuteurId, sujetIds));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
    @GetMapping("/visibles/etudiant")
    public ResponseEntity<?> getVisibleSujetsBySpecialite(
            @RequestParam String specialite ,@RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "6") int size
    ) {
        try{
            Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
            return ResponseEntity.ok(sujetServiceImp.getVisibleSujetsBySpecialitePaginated(specialite,pageable));

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/visible/suject/filtrage/liste")
    public ResponseEntity<?> getFiltrage(
            @RequestParam String specialite
    ) {
        try {
            FiltrageVisibleSubjectDTO dto = sujetServiceImp.getTitresAndThematiques(specialite);
            return ResponseEntity.ok(dto);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }




    @GetMapping("/filtrage/for/visible/subject/to/etudiant")
    public ResponseEntity<?> getFilteredVisibleSujets(
            @RequestParam String specialite,
            @RequestParam(required = false) List<String> titres,
            @RequestParam(required = false) List<String> thematiques,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
    try{
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sujetServiceImp.getFilteredVisibleSujets(specialite, titres, thematiques, pageable));

    }catch (Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    }

    @GetMapping("/visibleByTitle")

    public ResponseEntity<Page<Sujet>> searchSujet(
            @RequestParam String titre,
            @RequestParam String specialite,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<Sujet> result = sujetServiceImp.searchSujetByTitreAndSpecialite(titre, specialite, pageable);
        return ResponseEntity.ok(result);
    }

}
