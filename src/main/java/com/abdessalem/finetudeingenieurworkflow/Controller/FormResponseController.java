package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Entites.FormFieldResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.FormFieldResponseDTO;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.FormResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/response")
@RequiredArgsConstructor
public class FormResponseController {
        private final FormResponseService formResponseService;

    @PostMapping("/{formId}") public ResponseEntity<?> addFormResponse(@PathVariable Long formId,
                                                                          @RequestBody List<FormFieldResponse> responses)
    {
        formResponseService.addFormResponse(formId, responses);
        return ResponseEntity.ok().build();
    }

    @GetMapping("all/response/{formId}")
    public ResponseEntity<List<FormFieldResponseDTO>> getFormResponses(@PathVariable Long formId)
    {
        List<FormFieldResponseDTO> responses = formResponseService.getFormResponses(formId);
        return ResponseEntity.ok(responses);
    }
}
