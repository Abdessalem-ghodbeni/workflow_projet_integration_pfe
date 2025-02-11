package com.abdessalem.finetudeingenieurworkflow.Controller;


import com.abdessalem.finetudeingenieurworkflow.Entites.Societe;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ISocieteServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@CrossOrigin("*")
@RequestMapping("/societe")
@RequiredArgsConstructor
public class SocieteController {

    private final ISocieteServices societeServices;


//    @GetMapping(path = "all")
//    public ResponseEntity<?> recupererTousLesSocietes(){
//        List<Societe> listeSocietes= societeServices.GetAllSociete();
//        return ResponseEntity.ok(listeSocietes);
//    }
@GetMapping(path = "all")
    public ResponseEntity<Page<Societe>> recupererTousLesSocietes(

        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Societe> societesPage = societeServices.getAllSocietes(pageable);

    return ResponseEntity.ok(societesPage);
}

    @GetMapping(path = "/{id}")
    public  ResponseEntity<?>reupererParId(@PathVariable("id") Long id){
        Societe societe=societeServices.recupererById(id);
        return ResponseEntity.ok(societe);
    }


    @GetMapping("search")
    public ResponseEntity<Page<Societe>> recupererTousLesSocietes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Societe> societesPage;

        if (search == null || search.isEmpty()) {
            societesPage = societeServices.getAllSocietes(pageable);
        } else {
            societesPage = societeServices.searchSocietes(search, pageable);
        }

        return ResponseEntity.ok(societesPage);
    }



    @GetMapping("/filtered")
    public Page<Societe> getFilteredSocietes(
            @RequestParam(required = false) List<String> secteurs,
            @RequestParam(required = false) List<String> villes,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        return societeServices.getFilteredSocietes(secteurs, villes, active, pageable);
    }
}
