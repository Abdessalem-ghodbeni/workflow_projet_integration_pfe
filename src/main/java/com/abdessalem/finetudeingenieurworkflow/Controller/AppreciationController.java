package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Appreciation;
import com.abdessalem.finetudeingenieurworkflow.Entites.AppreciationDTO;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.AppreciationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appreciations")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AppreciationController {
    private final AppreciationService appreciationService;

    @PostMapping(path = "/ajouter")
    public ResponseEntity<?> createAppreciation(
            @Valid @RequestBody AppreciationDTO dto) {
       try {
           return new ResponseEntity<>(appreciationService.createAppreciation(dto),HttpStatus.CREATED);
       }catch (Exception exception){
           return new ResponseEntity<>(exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
       }

    }
}
