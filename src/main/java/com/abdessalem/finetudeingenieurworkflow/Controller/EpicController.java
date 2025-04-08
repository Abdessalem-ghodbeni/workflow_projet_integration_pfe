package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IEpicServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/epic")
@RequiredArgsConstructor
public class EpicController {
    private final IEpicServices epicServices;



    @PostMapping("/{projetId}/{etudiantId}")
    public ResponseEntity<ApiResponse> addEpic(@PathVariable Long projetId,
                                               @PathVariable Long etudiantId,
                                               @RequestBody Epic epic) {

        try {
            ApiResponse response = epicServices.addEpicToProject(projetId, etudiantId, epic);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{epicId}/{etudiantId}")
    public ResponseEntity<ApiResponse> updateEpic(@PathVariable Long epicId,
                                                  @PathVariable Long etudiantId,
                                                  @RequestBody Epic epic) {

        try {
            ApiResponse response = epicServices.updateEpic(epicId, etudiantId, epic);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{epicId}")
    public ResponseEntity<?> getEpicById(@PathVariable Long epicId) {
        try{
            return ResponseEntity.ok(epicServices.getEpicById(epicId));

        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }

    }

    @DeleteMapping("/{epicId}/{etudiantId}")
    public ResponseEntity<ApiResponse> deleteEpic(@PathVariable Long epicId,
                                                  @PathVariable Long etudiantId) {




        try {
            ApiResponse response = epicServices.deleteEpic(epicId, etudiantId);

            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    @GetMapping("/by/projetId/{projetId}")
    public ResponseEntity<?> getEpicsByProjet(@PathVariable Long projetId) {
      try{
            return ResponseEntity.ok(epicServices.getEpicsByProjetId(projetId));

        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
