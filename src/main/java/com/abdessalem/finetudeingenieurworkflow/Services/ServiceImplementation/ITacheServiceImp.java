package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.*;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITacheServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final IProjetRepository projetRepository;


    @Override
    @Transactional
    public ApiResponse ajouterTache(TacheRequest request, Long idEtudiant) {
        // Récupération de l'Épic
        Optional<Epic> epicOpt = epicRepository.findById(request.getEpicId());
        if (epicOpt.isEmpty()) {
            return new ApiResponse("Épic non trouvé", false);
        }

        // Récupération du Projet via l'idProjet fourni dans le DTO
        Optional<Projet> projetOpt = projetRepository.findById(request.getProjetId());
        if (projetOpt.isEmpty()) {
            return new ApiResponse("Projet non trouvé", false);
        }
        Projet projet = projetOpt.get();

        // Récupération du Backlog associé au Projet
        Backlog backlog = projet.getBacklog();
        if (backlog == null) {
            return new ApiResponse("Aucun backlog associé au projet", false);
        }

        // Création de la tâche en liant l'Épic et le Backlog
        Tache tache = Tache.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .complexite(request.getComplexite())
                .priorite(request.getPriorite())
                .epic(epicOpt.get())
                .backlog(backlog)
                .build();

        tacheRepository.save(tache);

        // Enregistrement de l'action dans l'historique
        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idEtudiant);
        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();
            historiqueServiceImp.enregistrerAction(
                    idEtudiant,
                    "CREATION",
                    etudiant.getNom() + " a ajouté la tâche '" + tache.getTitre() +
                            "' à l'épic '" + epicOpt.get().getNom() +
                            "' et au backlog du projet '" + projet.getNom() + "'"
            );
        }

        return new ApiResponse("Tâche ajoutée avec succès", true);
    }

    @Override
    @Transactional
    public ApiResponse modifierTache(Long tacheId, TacheRequest request, Long idEtudiant) {
        // Récupération de la tâche à modifier
        Optional<Tache> tacheOpt = tacheRepository.findById(tacheId);
        if (tacheOpt.isEmpty()) {
            return new ApiResponse("Tâche non trouvée", false);
        }

        // Récupération de l'Épic
        Optional<Epic> epicOpt = epicRepository.findById(request.getEpicId());
        if (epicOpt.isEmpty()) {
            return new ApiResponse("Épic non trouvé", false);
        }

        // Récupération du Projet à partir de l'ID fourni dans le DTO
        Optional<Projet> projetOpt = projetRepository.findById(request.getProjetId());
        if (projetOpt.isEmpty()) {
            return new ApiResponse("Projet non trouvé", false);
        }
        Projet projet = projetOpt.get();

        // Récupération du Backlog associé au projet
        Backlog backlog = projet.getBacklog();
        if (backlog == null) {
            return new ApiResponse("Aucun backlog associé au projet", false);
        }

        // Mise à jour de la tâche
        Tache tache = tacheOpt.get();
        tache.setTitre(request.getTitre());
        tache.setDescription(request.getDescription());
        tache.setComplexite(request.getComplexite());
        tache.setPriorite(request.getPriorite());
        tache.setEpic(epicOpt.get());
        tache.setBacklog(backlog);

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


    @Override
    public ApiResponse supprimerTache(Long tacheId, Long etudiantId) {
        Optional<Tache> tacheOpt = tacheRepository.findById(tacheId);
        if (tacheOpt.isEmpty()) {
            return new ApiResponse("Tâche non trouvée", false);
        }

        Tache tache = tacheOpt.get();
        String titreTache = tache.getTitre();

        tacheRepository.delete(tache);

        etudiantRepository.findById(etudiantId).ifPresent(etudiant -> {
            historiqueServiceImp.enregistrerAction(
                    etudiantId,
                    "SUPPRESSION",
                    etudiant.getNom() + " a supprimé la tâche '" + titreTache + "' (ID : " + tacheId + ")"
            );
        });

        return new ApiResponse("Tâche supprimée avec succès", true);
    }

    @Override
    public Tache getTacheById(Long tacheId) {
        return tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RessourceNotFound("Tâche non trouvée avec l'id : " + tacheId));
    }

    @Override
    public List<Tache> getTachesByProjetId(Long projetId) {
         Optional<Projet> projetOpt = projetRepository.findById(projetId);
        if (projetOpt.isEmpty()) {
            throw new RuntimeException("Projet non trouvé avec l'id : " + projetId);
        }
        Projet projet = projetOpt.get();

        Backlog backlog = projet.getBacklog();
        if (backlog == null) {
            throw new RuntimeException("Aucun backlog associé au projet avec l'id : " + projetId);
        }

        return backlog.getTaches();
    }
}
