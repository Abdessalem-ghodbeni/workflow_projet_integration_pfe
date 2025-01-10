package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Form;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/form")
@RequiredArgsConstructor
public class FormController {
    private final IFormService formService;


    @PostMapping(path = "ajouter-formulaire")
    public ResponseEntity<?> ajouterForm(@Valid @RequestBody Form formulaire) {
       try{
           Form savedForm = formService.ajouterForm(formulaire);
           return new ResponseEntity(savedForm,HttpStatus.CREATED);
       }catch (Exception exception){
           return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }


    }
}
