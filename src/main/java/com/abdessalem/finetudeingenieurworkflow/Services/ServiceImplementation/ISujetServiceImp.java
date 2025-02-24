package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISocieteRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISujetRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITuteurRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ISujetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ISujetServiceImp implements ISujetService {
private final ISujetRepository sujetRepository;
private final IUserRepository userRepository;
private final ITuteurRepository tuteurRepository;
private final ISocieteRepository societeRepository;


@Override
@Transactional
public Sujet createSujet(Sujet sujet, Long userId) {
    User utilisateur = userRepository.findById(userId)
            .orElseThrow(() -> new RessourceNotFound("Utilisateur non trouvé"));

    if (utilisateur instanceof Tuteur) {
        Tuteur tuteur = (Tuteur) utilisateur;
        sujet.setTuteur(tuteur);
        tuteur.getSujets().add(sujet);
    } else if (utilisateur instanceof Societe) {
        Societe societe = (Societe) utilisateur;
        sujet.setSociete(societe);
        societe.getSujets().add(sujet);
    } else {
        throw new RessourceNotFound("L'utilisateur n'est ni un tuteur ni une société");
    }


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

    @Override
    public Page<Sujet> getSujetsByTuteurId(Long tuteurId, int page) {
          tuteurRepository.findById(tuteurId)
                .orElseThrow(() -> new RessourceNotFound("tuteur avec l'ID " +tuteurId + " non trouvé."));
        int pageSize = 6;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return sujetRepository.findByTuteurId(tuteurId, pageRequest);
    }

    @Override
    public Page<Sujet> getSujetsBySocietId(Long societeId, int page) {
        societeRepository.findById(societeId)
                .orElseThrow(() -> new RessourceNotFound("Societe avec l'ID " +societeId + " non trouvé."));
        int pageSize = 6;
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return sujetRepository.findBySocieteId(societeId, pageRequest);
    }

    @Override
    public Page<Sujet> rechercherSujetParTitre(String titre, Pageable pageable) {
        return sujetRepository.findByTitreContainingIgnoreCase(titre, pageable);
    }

    @Override
    public Page<Sujet> getSujetsCreatedBySociete(Pageable pageable) {
        return sujetRepository.findBySocieteIsNotNull(pageable);
    }

    @Override
    public ApiResponse changerEtatSujet(Long idSujet, Etat nouvelEtat) {
        Sujet sujet = sujetRepository.findById(idSujet)
                .orElseThrow(() -> new RessourceNotFound("Sujet avec l'ID " + idSujet + " non trouvé."));
        if (nouvelEtat != null) {
            sujet.setEtat(nouvelEtat);
            sujetRepository.save(sujet);
            return new ApiResponse("Etat sujet mis à jour avec succès.", true);
        } else {
            return new ApiResponse("L'etat de sujet ne peux pas etre null ", false);
        }
    }

    @Override
    public FilterDTO getFilters() {
        List<String> thematiques = sujetRepository.findDistinctThematiques();
        List<Integer> annees = sujetRepository.findDistinctAnnees();
        List<String> societes = sujetRepository.findDistinctSocietes();
        List<String> specialites = sujetRepository.findDistinctSpecialites();
        List<Etat> etats = sujetRepository.findDistinctEtats();

        return new FilterDTO(thematiques, annees, societes, specialites, etats);
    }

    @Override
    public Page<Sujet> listSujetsCreatedByTureurs(Pageable pageable) {
        return sujetRepository.findByTuteurIsNotNull(pageable);
    }
@Override
    public Page<Sujet> getSujetsByFilters(
            List<String> thematiques,
            List<Integer> annees,
            List<String> societes,
            List<String> specialites,
            List<Etat> etats,
            Pageable pageable) {

        return sujetRepository.findByFilters(thematiques, annees, societes, specialites, etats, pageable);
    }

    @Override
    public FilterTutorDTO getFilterCriteria() {
        List<String> thematiques = sujetRepository.findDistinctThematiquesSujetCreatedByTuteur();
        List<Integer> annees = sujetRepository.findDistinctAnneesSujetCreatedByTuteur();
        List<String> titres = sujetRepository.findDistinctTitresSujetCreatedByTuteur();
        List<String> tuteurs = sujetRepository.findDistinctTuteursName();
        List<String> specialites = sujetRepository.findDistinctSpecialitesSujetCreatedByTuteur();
        List<Etat> etats = sujetRepository.findDistinctEtatsSujetCreatedByTuteur();

        return new FilterTutorDTO(thematiques, annees, titres, tuteurs, specialites, etats);
    }

    @Override
    public Page<Sujet> filterSujetsCreatedByAllTuteurs(List<String> thematiques, List<Integer> annees, List<String> titres, List<String> tuteurs, List<String> specialites, List<Etat> etats, Pageable pageable) {
        return sujetRepository.findByFiltersTuteurs(thematiques, annees, titres, tuteurs, specialites, etats, pageable);
    }


}
