package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEquipeRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IFormResponseRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IEquipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipeServiceImp implements IEquipeService {

    private final IEquipeRepository equipeRepository;
    private final IEtudiantRepository etudiantRepository;
    private final FormResponseService formResponseService;

@Override
@Transactional
public ApiResponse construireEquipes(Long formId) {
    List<FormResponseDTO> formResponses = formResponseService.getFormResponses(formId);

    if (formResponses.isEmpty()) {
        return new ApiResponse("Aucune réponse trouvée pour ce formulaire", false);
    }

    int totalAffectes = 0, totalIgnores = 0;

    for (FormResponseDTO formResponse : formResponses) {
        String nomEquipe = null;
        List<String> emailsEtudiants = new ArrayList<>();

        for (FormFieldResponseDTO response : formResponse.getResponses()) {
            String label = response.getLabel().trim();
            if ("Nom de l'équipe".equalsIgnoreCase(label)) {
                nomEquipe = response.getValue();
            } else if (label.toLowerCase().contains("email membre")) {
                emailsEtudiants.add(response.getValue());
            }
        }

        if (nomEquipe == null || emailsEtudiants.isEmpty()) {
            continue;
        }


        String finalNomEquipe = nomEquipe;
        Equipe equipe = equipeRepository.findByNom(nomEquipe)
                .orElseGet(() -> equipeRepository.save(Equipe.builder().nom(finalNomEquipe).build()));

        int affectes = 0, ignores = 0;
        for (String email : emailsEtudiants) {
            Optional<Etudiant> etudiantOpt = etudiantRepository.findByEmail(email);
            if (etudiantOpt.isPresent()) {
                Etudiant etudiant = etudiantOpt.get();
                if (etudiant.getEquipe() == null) {
                    etudiant.setEquipe(equipe);
                    etudiantRepository.save(etudiant);
                    affectes++;
                } else {
                    ignores++;
                }
            }
        }

        totalAffectes += affectes;
        totalIgnores += ignores;
    }

    return new ApiResponse(
            String.format("Équipes créées avec succès. %d étudiants affectés, %d ignorés (déjà dans une équipe).",
                    totalAffectes, totalIgnores),
            true
    );
}

}
