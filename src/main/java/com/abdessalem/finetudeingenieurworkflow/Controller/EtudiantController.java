package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.IEtudiantServicesImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/etudiant")
@RequiredArgsConstructor
public class EtudiantController {
    private final IEtudiantServicesImp etudiantServicesImp;


    @GetMapping(path = "all")
    public ResponseEntity<?> recupererTousEtudiants(){
        List<Etudiant> listeEtudiants= etudiantServicesImp.recuperListeEtudiant();
        return ResponseEntity.ok(listeEtudiants);
    }
    @GetMapping("/par-specialite")
    public ResponseEntity<?> getEtudiantsBySpecialite(@RequestParam String specialite) {
      try{
          return ResponseEntity.ok(etudiantServicesImp.getEtudiantsBySpecialite(specialite));

      }catch (Exception exception){
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
      }
    }
    @GetMapping(path = "/{id}")
    public  ResponseEntity<?>reupererParId(@PathVariable("id") Long id){
        Etudiant etudiant=etudiantServicesImp.getEtudiantById(id);
        return ResponseEntity.ok(etudiant);
    }

}
