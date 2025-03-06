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

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipeServiceImp implements IEquipeService {
    private final IFormResponseRepository formResponseRepository;
    private final IEquipeRepository equipeRepository;
    private final IEtudiantRepository etudiantRepository;

    @Override
    @Transactional
    public ApiResponse construireEquipes(Long formResponseId) {
        // Récupérer les réponses du formulaire
        FormResponse formResponse = formResponseRepository.findById(formResponseId)
                .orElseThrow(() -> new RuntimeException("FormResponse non trouvé"));

        List<FormFieldResponse> responses = formResponse.getResponses();

        // Extraction du nom de l’équipe et des emails des membres
        String nomEquipe = null;
        List<String> emailsEtudiants = new ArrayList<>();

        for (FormFieldResponse response : responses) {
            if (response.getFormField().getId() == 2) {
                nomEquipe = response.getValue(); // Nom de l'équipe
            } else if (response.getFormField().getId() == 4 || response.getFormField().getId() == 6) {
                emailsEtudiants.add(response.getValue()); // Emails des membres
            }
        }

        if (nomEquipe == null || emailsEtudiants.isEmpty()) {
            return new ApiResponse("Données invalides : Nom d'équipe ou emails manquants", false);
        }

        // Vérifier si l'équipe existe déjà
        String finalNomEquipe = nomEquipe;
        Equipe equipe = equipeRepository.findByNom(nomEquipe)
                .orElseGet(() -> {
                    Equipe newEquipe = Equipe.builder().nom(finalNomEquipe).build();
                    return equipeRepository.save(newEquipe);
                });

        // Associer les étudiants à l'équipe en ignorant ceux qui ont déjà une équipe
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

        return new ApiResponse(
                String.format("Équipe '%s' créée. %d étudiants affectés, %d ignorés (déjà dans une équipe).",
                        nomEquipe, affectes, ignores),
                true
        );
    }

}
