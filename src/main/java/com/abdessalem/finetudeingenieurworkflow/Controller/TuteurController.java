package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITuteurServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/tuteur")
@RequiredArgsConstructor
public class TuteurController {
    private final ITuteurServices tuteurServices;


    @GetMapping(path = "all")
    public ResponseEntity<?>recupererTousTuteur(){
        List<Tuteur> listeTuteur= tuteurServices.getAllTuteur();
        return ResponseEntity.ok(listeTuteur);
    }

@GetMapping(path = "/{id}")
    public  ResponseEntity<?>reupererParId(@PathVariable("id") Long id){
        Tuteur tuteur=tuteurServices.getTuteurById(id);
        return ResponseEntity.ok(tuteur);
}
}
