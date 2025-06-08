package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Appreciation;
import com.abdessalem.finetudeingenieurworkflow.Entites.AppreciationDTO;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
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

    @GetMapping("/{etudiantId}/appreciations")
    public ResponseEntity<?> getAppreciations(
            @PathVariable Long etudiantId) {
       try {
           return ResponseEntity.ok(appreciationService.listerParEtudiant(etudiantId));
       }catch (Exception exception){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
       }
    }

    @PutMapping(path = "/modifier/{id}")
    public ResponseEntity<?> updateAppreciation(@PathVariable Long id, @RequestBody AppreciationDTO dto) {
        try {
            Appreciation updated = appreciationService.updateAppreciation(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RessourceNotFound e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
