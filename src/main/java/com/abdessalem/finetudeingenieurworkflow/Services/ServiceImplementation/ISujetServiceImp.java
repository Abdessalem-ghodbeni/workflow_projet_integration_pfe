package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISujetRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ISujetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ISujetServiceImp implements ISujetService {
private final ISujetRepository sujetRepository;

    @Override
//    public Sujet createSujet(Long utilisateurId, Sujet sujet) {
    public Sujet createSujet( Sujet sujet) {
//        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
//                .orElseThrow(() -> new RessourceNotFound("Utilisateur avec l'ID " + utilisateurId + " non trouvé."));
//
//        sujet.setUtilisateur(utilisateur);
        return sujetRepository.save(sujet);
    }

    @Override
    public Sujet getSujetById(Long id) {
        return sujetRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFound("Sujet avec l'ID " + id + " non trouvé."));
    }

    @Override
    public void deleteSujet(Long id) {
        Sujet sujet = sujetRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFound("Sujet avec l'ID " + id + " non trouvé."));

         sujetRepository.delete(sujet);
    }

    @Override
    public List<Sujet> getAllSujets() {
        return sujetRepository.findAll();
    }

    @Override
    public Sujet updateSujet(Sujet sujet) {
        Sujet existingSujet = sujetRepository.findById(sujet.getId())
                .orElseThrow(() -> new RessourceNotFound("Sujet avec l'ID " + sujet.getId() + " non trouvé."));
        if (sujet.getExigences() != null) {
            existingSujet.setExigences(sujet.getExigences());
        }
        if(sujet.getTitre()!=null){
                existingSujet.setTitre(sujet.getTitre());
         }
        if(sujet.getDescription()!=null){
            existingSujet.setDescription(sujet.getDescription());
        }

        if (sujet.getTechnologies() != null) {
            existingSujet.setTechnologies(sujet.getTechnologies());
        }


        if(sujet.getThematique()!=null){
            existingSujet.setThematique(sujet.getThematique());

        }

        if(sujet.getSpecialite()!=null){
            existingSujet.setThematique(sujet.getThematique());
        }


        return sujetRepository.save(existingSujet);
    }
}
