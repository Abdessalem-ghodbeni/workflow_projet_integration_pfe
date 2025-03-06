package com.abdessalem.finetudeingenieurworkflow.Controller;
import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.FormAccessibilityRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.abdessalem.finetudeingenieurworkflow.Entites.Form;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IFormService;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.IHistoriqueServiceImp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/form")
@RequiredArgsConstructor
public class FormController {
    private final IFormService formService;


    @PostMapping(path = "/{idTuteur}/ajouter-formulaire")
    public ResponseEntity<?> ajouterForm(@PathVariable("idTuteur")Long idTuteur,@Valid @RequestBody Form formulaire) {
        Form savedForm = formService.ajouterForm(formulaire, idTuteur);

           return new ResponseEntity<>(savedForm,HttpStatus.CREATED);
    }


    @GetMapping("/tous/{tuteurId}")
    public ResponseEntity<?> getFormsByTuteur(
            @PathVariable Long tuteurId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        try
        {        Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(formService.getFormsByTuteur(tuteurId, pageable));
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }



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

    @DeleteMapping("delete/{id}/{idTuteur}")
    public ResponseEntity<Void> deleteForm(@PathVariable("id") Long id,@PathVariable("idTuteur")Long idTuteur) {
        try {
            formService.deleteFormById(id,idTuteur);
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

    @PutMapping("update/{idTuteur}")
    public ResponseEntity<?> updateForm(@Valid @RequestBody Form updatedForm,@PathVariable("idTuteur") Long idTuteur) {
       try {
           return new ResponseEntity<>(formService.updateForm(updatedForm,idTuteur),HttpStatus.OK);
       }catch (Exception exception){
           return new ResponseEntity<>(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }
    @PutMapping("/{id}/{idTuteur}/accessibility")
    public ResponseEntity<?> setFormAccessibility(
            @PathVariable("id") Long id,  @PathVariable("idTuteur") Long idTuteur,
            @RequestBody FormAccessibilityRequest request) {


        try {
            ApiResponse response = formService.setFormAccessibility(id,idTuteur, request);
            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getCause().getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





}
