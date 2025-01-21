package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITuteurServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
