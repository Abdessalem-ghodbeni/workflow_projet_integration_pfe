package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEquipeRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IProjetRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISujetRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITuteurRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IProjet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjetServiceImp implements IProjet {
           private final IEquipeRepository equipeRepository;
           private final IProjetRepository projetRepository;
           private final ISujetRepository sujetRepository;
           private final IHistoriqueServiceImp historiqueServiceImp;
    private final ITuteurRepository tuteurRepository;

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
    public ApiResponse affecterSujetAEquipe(String titreSujet, Long equipeId,Long tuteurId) {
        Sujet sujet = sujetRepository.findByTitre(titreSujet)
                .orElseThrow(() -> new RuntimeException("Sujet non trouvé avec le titre: " + titreSujet));


        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée"));

        Tuteur tuteur = tuteurRepository.findById(tuteurId)
                .orElseThrow(() -> new RuntimeException("tuteur non trouvé"));
        boolean projetExiste = projetRepository.existsByEquipeAndSujet(equipe, sujet);
        if (projetExiste) {
            return new ApiResponse(
                    String.format("Cette équipe travaille déjà sur ce sujet !"),
                    false
            );
        }


        String nomProjet = sujet.getTitre();


        Projet projet = Projet.builder()
                .nom(nomProjet)
                .equipe(equipe)
                .sujet(sujet)
                .build();

        projetRepository.save(projet);
        historiqueServiceImp.enregistrerAction(tuteurId, "MODIFICATION",
                tuteur.getNom() + " a affecté un sujet à l'équipe " + equipe.getNom());
        return new ApiResponse(
                String.format("Affectation effectué avec succes a l'equipe "+equipe.getNom()),
                true
        );
    }

    @Override
    public ApiResponse desaffecterSujetAEquipe(String titreSujet, Long equipeId,Long tuteurId) {
        Tuteur tuteur = tuteurRepository.findById(tuteurId)
                .orElseThrow(() -> new RuntimeException("tuteur non trouvé"));
        Sujet sujet = sujetRepository.findByTitre(titreSujet)
                .orElseThrow(() -> new RuntimeException("Sujet non trouvé avec le titre: " + titreSujet));

        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée"));


        Projet projet = projetRepository.findByEquipeAndSujet(equipe, sujet)
                .orElseThrow(() -> new RuntimeException("Aucun projet trouvé liant cette équipe et ce sujet"));


        projetRepository.delete(projet);
        historiqueServiceImp.enregistrerAction(tuteurId, "MODIFICATION",
                tuteur.getNom() + " a désaffecté un sujet de l'équipe " + equipe.getNom());
        return new ApiResponse(
                String.format("desaffectation effectué avec succes a l'equipe "+equipe.getNom()),
                true
        );
    }
}
