package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.IFormRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IFormResponseRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;
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
   private  final IUserRepository userRepository;
   private final IHistoriqueServiceImp historiqueServiceImp;
    public ApiResponse addFormResponse(Long formId, Long idUser, List<FormFieldResponse> responses) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));

        User utilisateur = userRepository.findById(idUser)
                .orElseThrow(() -> new RessourceNotFound("Utilisateur non trouvé"));

        FormResponse formResponse = new FormResponse();
        formResponse.setForm(form);

//        if (utilisateur instanceof Tuteur) {
//            Tuteur tuteur = (Tuteur) utilisateur;
//            formResponse.setTuteur(tuteur); // Supposant qu'il y a un champ `tuteur` dans `FormResponse`
//        } else
      if (utilisateur instanceof Etudiant) {
            Etudiant etudiant = (Etudiant) utilisateur;

            // Vérification si l'étudiant a déjà répondu à ce formulaire
            boolean exists = formResponseRepository.existsByEtudiantAndForm(etudiant, form);
            if (exists) {
                return new ApiResponse("L'étudiant a déjà répondu à ce formulaire.", false);
            }

            formResponse.setEtudiant(etudiant); // Supposant qu'il y a un champ `etudiant` dans `FormResponse`
        } else {
            return new ApiResponse("L'utilisateur doit être un tuteur ou un étudiant.", false);
        }

        // Associer les réponses au formulaire
        for (FormFieldResponse response : responses) {
            response.setFormResponse(formResponse);
        }
        formResponse.setResponses(responses);

        FormResponse formResponseInstance = formResponseRepository.save(formResponse);
        if (formResponseInstance != null) {
            historiqueServiceImp.enregistrerAction(idUser, "Création", utilisateur.getNom()+"a ajouter une reponse sur le frmulaire dont le numéro est : "+formId);
            return new ApiResponse("Réponse au formulaire ajoutée avec succès.", true);
        } else {
            return new ApiResponse("Échec de l'enregistrement de la réponse.", false);
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
