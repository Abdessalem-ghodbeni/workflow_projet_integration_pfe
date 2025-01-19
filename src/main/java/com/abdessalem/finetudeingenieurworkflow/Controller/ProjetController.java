package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Projet;
import com.abdessalem.finetudeingenieurworkflow.Entites.ProjetRequest;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.ProjetServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/projet")
@RequiredArgsConstructor
public class ProjetController {
    private final ProjetServiceImp projetServiceImp;

    @PostMapping
    public ResponseEntity<?> ajouterProjet(@RequestBody ProjetRequest projetRequest) {
        Projet savedProjet = projetServiceImp.ajouterProjet(projetRequest);
        return ResponseEntity.ok(savedProjet);
    }
}
