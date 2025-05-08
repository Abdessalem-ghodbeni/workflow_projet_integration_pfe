package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.*;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITacheServices;
import com.abdessalem.finetudeingenieurworkflow.utils.SendEmailServiceImp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    private final IUserRepository userRepository;
    private final SendEmailServiceImp sendEmailService;
    private final ISprintServicesImp sprintServicesImp;
private final EtatHistoriqueTacheRepository etatHistoriqueTacheRepository;

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
                .dateDebutEstimee(request.getDateDebutEstimee())
                .dateFinEstimee(request.getDateFinEstimee())
                .etat(EtatTache.TO_DO)
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
        tache.setEtat(EtatTache.TO_DO);
        tache.setEpic(epicOpt.get());
        tache.setBacklog(backlog);
        tache.setDateFinEstimee(request.getDateFinEstimee());
        tache.setDateDebutEstimee(request.getDateDebutEstimee());

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



    @Override
    @Transactional
    public ApiResponse affecterTacheAEtudiant(Long idTache, Long idEtudiantCible, Long idUserQuiAffecte) {
        Optional<Tache> tacheOpt = tacheRepository.findById(idTache);
        if (tacheOpt.isEmpty()) {
            return new ApiResponse("Tâche non trouvée", false);
        }

        Optional<Etudiant> etudiantCibleOpt = etudiantRepository.findById(idEtudiantCible);
        if (etudiantCibleOpt.isEmpty()) {
            return new ApiResponse("Étudiant cible non trouvé", false);
        }

        Optional<User> userQuiAffecteOpt = userRepository.findById(idUserQuiAffecte);
        if (userQuiAffecteOpt.isEmpty()) {
            return new ApiResponse("Utilisateur qui affecte non trouvé", false);
        }

        Tache tache = tacheOpt.get();
        Etudiant etudiantCible = etudiantCibleOpt.get();
        User userQuiAffecte = userQuiAffecteOpt.get();

        // Affectation
        tache.setEtudiant(etudiantCible);
        tacheRepository.save(tache);

        // Historique personnalisé selon le rôle
        String message;
        if (userQuiAffecte instanceof Etudiant etudiantQuiAffecte) {
            if (etudiantQuiAffecte.getId().equals(etudiantCible.getId())) {
                message = etudiantQuiAffecte.getNom() + " s’est auto-affecté la tâche '" + tache.getTitre() + "'";
            } else {
                message = etudiantQuiAffecte.getNom() + " a affecté la tâche '" + tache.getTitre() + "' à son camarade " + etudiantCible.getNom();
            }
        } else {
            message = "Le tuteur " + userQuiAffecte.getNom() + " a affecté la tâche '" + tache.getTitre() + "' à l’étudiant " + etudiantCible.getNom();
        }

        historiqueServiceImp.enregistrerAction(
                userQuiAffecte.getId(),
                "AFFECTATION_TACHE_ETUDIANT",
                message
        );

        return new ApiResponse("Tâche affectée à l'étudiant avec succès", true);
    }

//    @Override
//    public ApiResponse changerEtatTache(Long idTache, EtatTache nouvelEtat, Long idEtudiant) {
//        Optional<Tache> tacheOpt = tacheRepository.findById(idTache);
//        if (tacheOpt.isEmpty()) {
//            return new ApiResponse("Tâche non trouvée", false);
//        }
//
//        Tache tache = tacheOpt.get();
//        EtatTache ancienEtat = tache.getEtat();
//
//        // On ne fait rien si l'état ne change pas
//        if (ancienEtat == nouvelEtat) {
//            return new ApiResponse("L'état de la tâche est déjà '" + nouvelEtat + "'", false);
//        }
//
//        tache.setEtat(nouvelEtat);
//        tacheRepository.save(tache);
//
//        // Historique
//        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idEtudiant);
//        if (etudiantOpt.isPresent()) {
//            Etudiant etudiant = etudiantOpt.get();
//            String message = etudiant.getNom() + " a changé l'état de la tâche '" +
//                    tache.getTitre() + "' (ID : " + tache.getId() + ") de '" + ancienEtat +
//                    "' vers '" + nouvelEtat + "'";
//
//            historiqueServiceImp.enregistrerAction(
//                    idEtudiant,
//                    "CHANGEMENT_ETAT_TACHE",
//                    message
//
//            );
//            EtatHistoriqueTache historique = EtatHistoriqueTache.builder()
//                    .ancienEtat(ancienEtat)
//                    .nouveauEtat(nouvelEtat)
//                    .dateChangement(LocalDateTime.now())
//                    .acteur(etudiantOpt.orElse(null))
//                    .tache(tache)
//                    .build();
//            etatHistoriqueTacheRepository.save(historique);
//        }
//
//
//        return new ApiResponse("État de la tâche mis à jour avec succès", true);
//    }
@Override
@Transactional
public ApiResponse changerEtatTache(Long idTache, EtatTache nouvelEtat, Long idEtudiant) {
    Optional<Tache> tacheOpt = tacheRepository.findById(idTache);
    if (tacheOpt.isEmpty()) {
        return new ApiResponse("Tâche non trouvée", false);
    }

    Tache tache = tacheOpt.get();
    EtatTache ancienEtat = tache.getEtat();

    // Ne rien faire si l'état ne change pas
    if (ancienEtat == nouvelEtat) {
        return new ApiResponse("L'état de la tâche est déjà '" + nouvelEtat + "'", false);
    }

    // Sauvegarder le nouvel état
    tache.setEtat(nouvelEtat);
    tacheRepository.save(tache);

    // --- LOGIQUE POUR METTRE À JOUR LE TAUX D'AVANCEMENT ---
    boolean besoinDeMettreAJourAvancement = false;

    if (nouvelEtat == EtatTache.VALIDATED || ancienEtat == EtatTache.VALIDATED) {
        besoinDeMettreAJourAvancement = true;
    }

    if (besoinDeMettreAJourAvancement && tache.getSprint() != null) {
        sprintServicesImp.mettreAJourTauxAvancement(tache.getSprint().getId());
    }
    // -----------------------------------------------------

    // Historique utilisateur
    Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idEtudiant);
    if (etudiantOpt.isPresent()) {
        Etudiant etudiant = etudiantOpt.get();
        String message = etudiant.getNom() + " a changé l'état de la tâche '" +
                tache.getTitre() + "' (ID : " + tache.getId() + ") de '" + ancienEtat +
                "' vers '" + nouvelEtat + "'";

        historiqueServiceImp.enregistrerAction(
                idEtudiant,
                "CHANGEMENT_ETAT_TACHE",
                message
        );

        EtatHistoriqueTache historique = EtatHistoriqueTache.builder()
                .ancienEtat(ancienEtat)
                .nouveauEtat(nouvelEtat)
                .dateChangement(LocalDateTime.now())
                .acteur(etudiantOpt.orElse(null))
                .tache(tache)
                .build();
        etatHistoriqueTacheRepository.save(historique);
    }

    return new ApiResponse("État de la tâche mis à jour avec succès", true);
}


// 17:13 te3 la3chiya
    @Scheduled(cron = "0 13 17 * * *")
    @Transactional
    public void checkOverdueTasks() {
        final LocalDate today = LocalDate.now();
        final List<EtatTache> completedStates = List.of(EtatTache.VALIDATED, EtatTache.DONE);

        log.info("Lancement de la vérification des tâches en retard à {}", LocalDateTime.now());

        List<Tache> tasks = tacheRepository.findOverdueTasks(today, completedStates);

        log.info("{} tâches potentielles trouvées", tasks.size());

        tasks.forEach(task -> {
            try {
                Etudiant student = task.getEtudiant();
                Equipe team = student.getEquipe();

                log.debug("Traitement de la tâche {} - Étudiant {}", task.getId(), student.getId());

                if(team == null || team.getTuteur() == null) {
                    log.warn("Équipe/tuteur manquant pour la tâche {} (Étudiant {})", task.getId(), student.getId());
                    return;
                }

                Tuteur tutor = team.getTuteur();

                if(isValidEmail(student.getEmail()) && isValidEmail(tutor.getEmail())) {
                    sendEmailService.sendTaskOverdueEmail(student.getEmail(), tutor.getEmail(), task);
                    task.setNotified(true);
                    tacheRepository.save(task);
                    log.info("Notification envoyée pour la tâche {}", task.getId());
                } else {
                    log.error("Email invalide pour la tâche {}", task.getId());
                }

            } catch (Exception e) {
                log.error("Échec du traitement de la tâche {} : {}", task.getId(), e.getMessage());
            }
        });

        log.info("Vérification terminée. Tâches traitées : {}", tasks.size());
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }



}
