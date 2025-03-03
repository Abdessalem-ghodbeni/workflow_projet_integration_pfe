package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.FormFieldResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.FormFieldResponseDTO;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.FormResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/response")
@RequiredArgsConstructor
public class FormResponseController {
        private final FormResponseService formResponseService;

    @PostMapping("/repondre/{formId}") public ResponseEntity<?> addFormResponse(@PathVariable Long formId,
                                                                          @RequestBody List<FormFieldResponse> responses)
    {
//        formResponseService.addFormResponse(formId, responses);
//        return ResponseEntity.status(HttpStatus.CREATED).build();

        try {
            ApiResponse response = formResponseService.addFormResponse(formId, responses);
            if (!response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
//                historiqueServiceImp.enregistrerAction(idUser, "Modification", "Changement du d'etat sujet en "+nouvelEtat);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getCause().getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("all/response/{formId}")
    public ResponseEntity<List<FormFieldResponseDTO>> getFormResponses(@PathVariable Long formId)
    {
        List<FormFieldResponseDTO> responses = formResponseService.getFormResponses(formId);
        return ResponseEntity.ok(responses);
    }
}
