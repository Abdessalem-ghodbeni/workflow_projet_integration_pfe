package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITuteurServices;
import lombok.RequiredArgsConstructor;
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





}
