package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITuteurServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/tuteur")
@RequiredArgsConstructor
public class TuteurController {
    private final ITuteurServices tuteurServices;


    @GetMapping(path = "totale/liste")
    public ResponseEntity<?>recupererTousTuteur(){
        List<Tuteur> listeTuteur= tuteurServices.getAllTuteur();
        return ResponseEntity.ok(listeTuteur);
    }

@GetMapping(path = "/{id}")
    public  ResponseEntity<?>reupererParId(@PathVariable("id") Long id){
        Tuteur tuteur=tuteurServices.getTuteurById(id);
        return ResponseEntity.ok(tuteur);
}
    @GetMapping("liste/chef-options")
    public ResponseEntity<?> getAllChefOptionsTuteurs() {
     try{
         return ResponseEntity.ok(tuteurServices.getAllChefOptionsTuteurs());
     }catch (Exception exception){
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
     }
    }

    @PutMapping("/toggleChefOption/{tuteurId}/{actionneurIdTuteur}")
    public ResponseEntity<?> toggleChefOption(@PathVariable("tuteurId") Long tuteurId,@PathVariable("actionneurIdTuteur") Long actionneurIdTuteur) {
        try {
            ApiResponse response = tuteurServices.toggleChefOption(tuteurId,actionneurIdTuteur);
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
    public ResponseEntity<Page<Tuteur>> recupererTousLesTuteurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Tuteur> tuteursPage = tuteurServices.getAllTuteurs(pageable);

        return ResponseEntity.ok(tuteursPage);
    }

    @GetMapping("search")
    public ResponseEntity<Page<Tuteur>> recupererTousLesTuteurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Tuteur> tuteursPage;

        if (search == null || search.isEmpty()) {
            tuteursPage = tuteurServices.getAllTuteurs(pageable);
        } else {
            tuteursPage = tuteurServices.searchTuteurs(search, pageable);
        }

        return ResponseEntity.ok(tuteursPage);
    }

    @GetMapping("/{tuteurId}/stats")
    public ResponseEntity<?> getTuteurStats(
            @PathVariable Long tuteurId,
            @RequestParam(required = false) Integer year
    ) {
        try {
           return ResponseEntity.ok(tuteurServices.getTuteurStats(tuteurId, year));
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }



}
