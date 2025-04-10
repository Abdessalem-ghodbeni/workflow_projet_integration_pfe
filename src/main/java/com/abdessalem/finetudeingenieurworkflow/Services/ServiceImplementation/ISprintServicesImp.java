package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IProjetRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISprintRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITacheRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ISprintServices;
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
public class ISprintServicesImp implements ISprintServices {
    private final ISprintRepository sprintRepository;
    private final IHistoriqueServiceImp historiqueServiceImp;
    private  final IEtudiantRepository etudiantRepository;
    private final IProjetRepository projetRepository;
    private final ITacheRepository tacheRepository;

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

    @Override
    public List<Sprint> getSprintsByProjetId(Long projetId) {
        Optional<Projet> projetOpt = projetRepository.findById(projetId);
        if (projetOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Projet projet = projetOpt.get();
        return projet.getSprints();
    }
    @Override
    @Transactional
    public ApiResponse supprimerSprint(Long sprintId, Long idEtudiant) {
        Optional<Sprint> sprintOpt = sprintRepository.findById(sprintId);
        if (sprintOpt.isEmpty()) {
            return new ApiResponse("Sprint non trouvé", false);
        }

        Sprint sprint = sprintOpt.get();

        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idEtudiant);
        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();
            historiqueServiceImp.enregistrerAction(
                    idEtudiant,
                    "SUPPRESSION",
                    etudiant.getNom() + " a supprimé le sprint '" + sprint.getNom() + "' (ID : " + sprint.getId() + ")"
            );
        }

        sprintRepository.delete(sprint);

        return new ApiResponse("Sprint supprimé avec succès", true);
    }
    @Override
    @Transactional
    public ApiResponse affecterTacheAuSprint(Long idTache, Long idSprint, Long idEtudiant) {
        Optional<Tache> tacheOpt = tacheRepository.findById(idTache);
        if (tacheOpt.isEmpty()) {
            return new ApiResponse("Tâche non trouvée", false);
        }

        Optional<Sprint> sprintOpt = sprintRepository.findById(idSprint);
        if (sprintOpt.isEmpty()) {
            return new ApiResponse("Sprint non trouvé", false);
        }

        Tache tache = tacheOpt.get();
        Sprint sprint = sprintOpt.get();

        tache.setSprint(sprint);
        tacheRepository.save(tache);

        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idEtudiant);
        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();
            historiqueServiceImp.enregistrerAction(
                    idEtudiant,
                    "AFFECTATION",
                    etudiant.getNom() + " a affecté la tâche '" + tache.getTitre() + "' (ID : " + tache.getId() + ") au sprint '" + sprint.getNom() + "' (ID : " + sprint.getId() + ")"
            );
        }

        return new ApiResponse("Tâche affectée au sprint avec succès", true);
    }

}
