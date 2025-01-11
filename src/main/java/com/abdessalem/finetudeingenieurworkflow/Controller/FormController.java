package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Form;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(path = "all/forms")
    public  ResponseEntity<?>getallFormulaire(){
        try{
            List<Form> formulaires = formService.getAllForms();
            if (formulaires.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("Liste des formulaire est vide ");
            }
            return ResponseEntity.ok(formulaires);
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        try {
            formService.deleteFormById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }
}
