package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Historique;
import com.abdessalem.finetudeingenieurworkflow.Entites.Societe;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ISocieteServices;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.IHistoriqueServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/historique")
@RequiredArgsConstructor
public class HistoriqueController {
    private final IHistoriqueServiceImp historiqueService;

    @GetMapping(path = "/{id_user}")
    public ResponseEntity<?> reupererParId(@PathVariable("id_user") Long id){
        try{
            List<Historique> historiqueListe=historiqueService.historiqueByUser(id);
            return ResponseEntity.ok(historiqueListe);
        }catch (Exception exception){
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }


}
