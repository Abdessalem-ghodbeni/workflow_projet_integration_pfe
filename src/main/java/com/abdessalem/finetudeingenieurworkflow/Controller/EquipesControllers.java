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

    @PostMapping("/construire/{formResponseId}")
    public ResponseEntity<?> construireEquipes(@PathVariable("formResponseId") Long formResponseId) {
       try {
            ApiResponse response = equipeService.construireEquipes(formResponseId);
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
