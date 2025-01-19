package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.Equipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.Projet;
import com.abdessalem.finetudeingenieurworkflow.Entites.ProjetRequest;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEquipeRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IProjetRepository;
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
    //ajouter projet et effectation de l'equipe
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
}
