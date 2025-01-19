package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Form;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        Form savedForm = formService.ajouterForm(formulaire);
           return new ResponseEntity(savedForm,HttpStatus.CREATED);
    }
    @GetMapping(path = "/all/forms")
    public ResponseEntity<?> getallFormulaire() {
        try {
            List<Form> formulaires = formService.getAllForms();
            return ResponseEntity.ok()
                    .body(formulaires);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        try {
            formService.deleteFormById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(path = "by/id/{id}")
    public ResponseEntity<?>getFormulaireById(@PathVariable("id")long id){
            Form formulaire=formService.getFormById(id);
            return ResponseEntity.ok(formulaire);
    }

    @PutMapping("update")
    public ResponseEntity<Form> updateForm(@Valid @RequestBody Form updatedForm) {
        Form updated = formService.updateForm(updatedForm);
        return ResponseEntity.ok(updated);
    }


}
