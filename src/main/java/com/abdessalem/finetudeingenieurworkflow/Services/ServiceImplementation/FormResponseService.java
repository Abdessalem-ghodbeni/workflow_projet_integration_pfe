package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.IFormRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IFormResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FormResponseService {
     private final IFormRepository formRepository;
   private final IFormResponseRepository formResponseRepository;
    public ApiResponse addFormResponse(Long formId, List<FormFieldResponse> responses) {
        Form form = formRepository.findById(formId).orElseThrow(() -> new RuntimeException("Form not found"));
        FormResponse formResponse = new FormResponse();
        formResponse.setForm(form);
        for (FormFieldResponse response : responses) {
            response.setFormResponse(formResponse);
        }
        formResponse.setResponses(responses);
       FormResponse formResponseInstance= formResponseRepository.save(formResponse);
    if(formResponseInstance!=null){
        return new ApiResponse("Reponse formulaire est ajouté  avec succès.", true);
    }else {
        return new ApiResponse("La reponse de formulaire n'est pas enregistré ", false);
    }

    }
    public List<FormFieldResponseDTO>  getFormResponses(Long formId) {
        List<FormResponse> formResponses = formResponseRepository.findByFormId(formId);
        return formResponses.stream().flatMap(formResponse -> formResponse.getResponses().stream())
                .map(this::convertToDTO).collect(Collectors.toList());
    }
    private FormFieldResponseDTO convertToDTO(FormFieldResponse response) {
        return FormFieldResponseDTO.builder() .id(response.getFormField().getId())
                .label(response.getFormField().getLabel()) .value(response.getValue()) .build();
    }

}
