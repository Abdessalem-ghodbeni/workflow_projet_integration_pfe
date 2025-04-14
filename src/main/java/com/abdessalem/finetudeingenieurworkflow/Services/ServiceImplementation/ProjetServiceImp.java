package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.*;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IProjet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjetServiceImp implements IProjet {
           private final IEquipeRepository equipeRepository;
           private final IProjetRepository projetRepository;
           private final ISujetRepository sujetRepository;
           private final IHistoriqueServiceImp historiqueServiceImp;
           private final IEtudiantRepository etudiantRepository;
           private final ITuteurRepository tuteurRepository;
           private final IBacklogRepository backlogRepository;

    @Override
    public Projet ajouterProjet(ProjetRequest projetRequest) {
        Equipe equipe = equipeRepository.findById(projetRequest.getEquipeId())
                .orElseThrow(() -> new RessourceNotFound("Equipe non trouvée"));

        Projet projet = new Projet();
        projet.setNom(projetRequest.getNom());
        projet.setEquipe(equipe);

        return projetRepository.save(projet);
    }

    @Override
    public Projet supprimerProjet(Long id) {
        return null;
    }

    @Override
    public Projet modifierUnProjet(Projet projet) {
        return null;
    }

    @Override
    public List<Projet> recupererProjet() {
        return null;
    }

    @Override
    public Projet recupererUnProjet(Long id) {
        return null;
    }

    @Override
    public ApiResponse affecterSujetAEquipe(String titreSujet, Long equipeId, Long tuteurId) {
        Sujet sujet = sujetRepository.findByTitre(titreSujet)
                .orElseThrow(() -> new RuntimeException("Sujet non trouvé avec le titre: " + titreSujet));

        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée"));

        Tuteur tuteur = tuteurRepository.findById(tuteurId)
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé"));

        boolean projetExiste = projetRepository.existsByEquipeAndSujet(equipe, sujet);
        if (projetExiste) {
            return new ApiResponse(
                    "Cette équipe travaille déjà sur ce sujet !",
                    false
            );
        }

        String nomProjet = sujet.getTitre();

        // On crée d'abord un projet SANS backlog
        Projet projet = Projet.builder()
                .nom(nomProjet)
                .equipe(equipe)
                .sujet(sujet)
                .build();

        // Ensuite, on crée le backlog
        Backlog backlog = Backlog.builder()
                .build();

        // On associe le backlog au projet, et le projet au backlog
        projet.setBacklog(backlog);
        backlog.setProjet(projet); // facultatif vu que mappedBy, mais propre

        // On sauvegarde le projet qui va cascade sur backlog
        projetRepository.save(projet);

        historiqueServiceImp.enregistrerAction(tuteurId, "MODIFICATION",
                tuteur.getNom() + " a affecté un sujet à l'équipe " + equipe.getNom());

        return new ApiResponse(
                "Affectation effectuée avec succès à l'équipe " + equipe.getNom(),
                true
        );
    }


    //    @Override
//    public ApiResponse desaffecterSujetAEquipe(String titreSujet, Long equipeId,Long tuteurId) {
//        Tuteur tuteur = tuteurRepository.findById(tuteurId)
//                .orElseThrow(() -> new RuntimeException("tuteur non trouvé"));
//        Sujet sujet = sujetRepository.findByTitre(titreSujet)
//                .orElseThrow(() -> new RuntimeException("Sujet non trouvé avec le titre: " + titreSujet));
//
//        Equipe equipe = equipeRepository.findById(equipeId)
//                .orElseThrow(() -> new RuntimeException("Équipe non trouvée"));
//
//
//        Projet projet = projetRepository.findByEquipeAndSujet(equipe, sujet)
//                .orElseThrow(() -> new RuntimeException("Aucun projet trouvé liant cette équipe et ce sujet"));
//
//
//        projetRepository.delete(projet);
//        historiqueServiceImp.enregistrerAction(tuteurId, "MODIFICATION",
//                tuteur.getNom() + " a désaffecté un sujet de l'équipe " + equipe.getNom());
//        return new ApiResponse(
//                String.format("desaffectation effectué avec succes a l'equipe "+equipe.getNom()),
//                true
//        );
//    }
@Override
public ApiResponse desaffecterSujetAEquipe(String titreSujet, Long equipeId, Long tuteurId) {

    Tuteur tuteur = tuteurRepository.findById(tuteurId)
            .orElseThrow(() -> new RuntimeException("Tuteur non trouvé"));

    Sujet sujet = sujetRepository.findByTitre(titreSujet)
            .orElseThrow(() -> new RuntimeException("Sujet non trouvé avec le titre: " + titreSujet));

    Equipe equipe = equipeRepository.findById(equipeId)
            .orElseThrow(() -> new RuntimeException("Équipe non trouvée"));


    Projet projet = projetRepository.findByEquipeAndSujet(equipe, sujet)
            .orElseThrow(() -> new RuntimeException("Aucun projet trouvé liant cette équipe et ce sujet"));

    try {

        projetRepository.delete(projet);


        if (!projetRepository.existsById(projet.getId())) {
            historiqueServiceImp.enregistrerAction(tuteurId, "MODIFICATION",
                    tuteur.getNom() + " a désaffecté un sujet de l'équipe " + equipe.getNom());
            return new ApiResponse(
                    String.format("Désaffectation effectuée avec succès pour l'équipe %s", equipe.getNom()),
                    true
            );
        } else {
            return new ApiResponse(
                    String.format("Erreur lors de la suppression du projet pour l'équipe %s", equipe.getNom()),
                    false
            );
        }
    } catch (Exception e) {

        return new ApiResponse(
                String.format("Une erreur s'est produite lors de la désaffectation du sujet pour l'équipe %s : %s", equipe.getNom(), e.getMessage()),
                false
        );
    }
}


    @Override
    public ApiResponse updateLienGitHub(Long projetId,Long etudiantid, String nouveauLienGitHub) {
        Etudiant etudiant = etudiantRepository.findById(etudiantid)
                .orElseThrow(() -> new RuntimeException("etudiant non trouvé"));
        Optional<Projet> projetOpt = projetRepository.findById(projetId);

        if (projetOpt.isPresent()) {
            Projet projet = projetOpt.get();
            projet.setLienGitHub(nouveauLienGitHub);
            projetRepository.save(projet);
            historiqueServiceImp.enregistrerAction(etudiantid, "MODIFICATION",
                    etudiant.getNom() + " a mettre a jour le lien github de son projet " );
            return new ApiResponse("Le lien GitHub a été mis à jour avec succès.", true);
        }

        return new ApiResponse("Projet non trouvé.", false);

    }

    @Override
    public Projet getProjetByEtudiantId(Long etudiantId) {
        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(etudiantId);

        if (etudiantOpt.isPresent() && etudiantOpt.get().getEquipe() != null) {

            return etudiantOpt.get().getEquipe().getProjets().stream().findFirst().orElse(null);
        }

        return null;
    }
@Override
    public Projet getProjetById(Long id){
        return projetRepository.findById(id).orElseThrow(() -> new RuntimeException("projet non trouvé"));
        }

}
