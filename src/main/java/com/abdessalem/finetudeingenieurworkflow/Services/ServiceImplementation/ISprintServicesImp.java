package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Projet;
import com.abdessalem.finetudeingenieurworkflow.Entites.Sprint;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IProjetRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISprintRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ISprintServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ISprintServicesImp implements ISprintServices {
    private final ISprintRepository sprintRepository;
    private final IHistoriqueServiceImp historiqueServiceImp;
    private  final IEtudiantRepository etudiantRepository;
    private final IProjetRepository projetRepository;

    @Override
    @Transactional
    public ApiResponse ajouterSprint(Sprint request, Long projetId, Long etudiantId) {
        Optional<Projet> projetOpt = projetRepository.findById(projetId);
        if (projetOpt.isEmpty()) {
            return new ApiResponse("Projet non trouvé", false);
        }

        Sprint sprint = Sprint.builder()
                .nom(request.getNom())
                .objectif(request.getObjectif())
                .dateDebutPlanifiee(request.getDateDebutPlanifiee())
                .dateFinPlanifiee(request.getDateFinPlanifiee())
                .projet(projetOpt.get())
                .build();

        sprintRepository.save(sprint);

        etudiantRepository.findById(etudiantId).ifPresent(etudiant -> {
            historiqueServiceImp.enregistrerAction(
                    etudiantId,
                    "CREATION",
                    etudiant.getNom() + " a ajouté le sprint '" + sprint.getNom() + "' au projet '" + projetOpt.get().getNom() + "'"
            );
        });

        return new ApiResponse("Sprint ajouté avec succès", true);
    }

    @Override
    @Transactional
    public ApiResponse modifierSprint(Long sprintId, Sprint request, Long etudiantId) {
        Optional<Sprint> sprintOpt = sprintRepository.findById(sprintId);
        if (sprintOpt.isEmpty()) {
            return new ApiResponse("Sprint non trouvé", false);
        }

        Sprint sprint = sprintOpt.get();
        sprint.setNom(request.getNom());
        sprint.setObjectif(request.getObjectif());
        sprint.setDateDebutPlanifiee(request.getDateDebutPlanifiee());
        sprint.setDateFinPlanifiee(request.getDateFinPlanifiee());

        sprintRepository.save(sprint);

        etudiantRepository.findById(etudiantId).ifPresent(etudiant -> {
            historiqueServiceImp.enregistrerAction(
                    etudiantId,
                    "MODIFICATION",
                    etudiant.getNom() + " a modifié le sprint '" + sprint.getNom() + "' (ID : " + sprint.getId() + ")"
            );
        });

        return new ApiResponse("Sprint modifié avec succès", true);
    }
}
