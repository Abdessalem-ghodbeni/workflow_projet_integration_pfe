package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IEquipeService;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.EquipeServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/equipe")
@RequiredArgsConstructor
public class EquipesControllers {

private final IEquipeService equipeService;

    @PostMapping("/construire/{formId}")
    public ResponseEntity<?> construireEquipes(@PathVariable("formId") Long formId) {
       try {
            ApiResponse response = equipeService.construireEquipes(formId);
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


}
