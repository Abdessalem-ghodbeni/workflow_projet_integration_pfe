package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.IBacklogRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEpicRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITacheRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITacheServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ITacheServiceImp implements ITacheServices {
    private final ITacheRepository tacheRepository;
    private  final IEpicRepository epicRepository;
    private final IHistoriqueServiceImp historiqueServiceImp;
    private final IBacklogRepository backlogRepository;
    private  final IEtudiantRepository etudiantRepository;


    @Override
    @Transactional
    public ApiResponse ajouterTache(TacheRequest request, Long idEtudiant) {
        Optional<Epic> epicOpt = epicRepository.findById(request.getEpicId());
        if (epicOpt.isEmpty()) {
            return new ApiResponse("Épic non trouvé", false);
        }

        Optional<Backlog> backlogOpt = backlogRepository.findById(request.getBacklogId());
        if (backlogOpt.isEmpty()) {
            return new ApiResponse("Backlog non trouvé", false);
        }

        Tache tache = Tache.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .complexite(request.getComplexite())
                .epic(epicOpt.get())
                .backlog(backlogOpt.get())
                .build();

       tacheRepository.save(tache);

        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idEtudiant);
        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();

            historiqueServiceImp.enregistrerAction(
                    idEtudiant,
                    "CREATION",
                    etudiant.getNom() + " a ajouté la tâche '" + tache.getTitre() +
                            "' à l'épic '" + epicOpt.get().getNom() +
                            "' et au backlog ID : " + backlogOpt.get().getId()
            );
        }

        return new ApiResponse("Tâche ajoutée avec succès", true);
    }

    @Override
    public ApiResponse modifierTache(Long tacheId, TacheRequest request, Long idEtudiant) {
        Optional<Tache> tacheOpt = tacheRepository.findById(tacheId);
        if (tacheOpt.isEmpty()) {
            return new ApiResponse("Tâche non trouvée", false);
        }
        Optional<Epic> epicOpt = epicRepository.findById(request.getEpicId());
        if (epicOpt.isEmpty()) {
            return new ApiResponse("Épic non trouvé", false);
        }

        Optional<Backlog> backlogOpt = backlogRepository.findById(request.getBacklogId());
        if (backlogOpt.isEmpty()) {
            return new ApiResponse("Backlog non trouvé", false);
        }

        Tache tache = tacheOpt.get();
        tache.setTitre(request.getTitre());
        tache.setDescription(request.getDescription());
        tache.setComplexite(request.getComplexite());
        tache.setEpic(epicOpt.get());
        tache.setBacklog(backlogOpt.get());

        tacheRepository.save(tache);

        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idEtudiant);
        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();
            historiqueServiceImp.enregistrerAction(
                    idEtudiant,
                    "MODIFICATION",
                    etudiant.getNom() + " a modifié la tâche '" + tache.getTitre() + "' (ID : " + tache.getId() + ")"
            );
        }

        return new ApiResponse("Tâche modifiée avec succès", true);
    }
}
