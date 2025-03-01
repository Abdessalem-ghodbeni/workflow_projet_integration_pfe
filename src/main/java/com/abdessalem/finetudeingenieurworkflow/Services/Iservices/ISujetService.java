package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import org.springframework.data.domain.Page;

import java.util.List;
import org.springframework.data.domain.Pageable;
public interface ISujetService {

Sujet createSujet( Sujet sujet,Long userId);
    Sujet getSujetById(Long id);
    void deleteSujet(Long id);
    Page<Sujet> getAllSujets(Pageable pageable);
    Sujet updateSujet(Sujet sujet);
    Page<Sujet> getSujetsByTuteurId(Long tuteurId, int page);
    Page<Sujet> getSujetsBySocietId(Long societeId, int page);
    Page<Sujet> rechercherSujetParTitre(String titre, Pageable pageable);
    Page<Sujet> getSujetsCreatedBySociete(Pageable pageable);
    ApiResponse changerEtatSujet(Long idSujet, Etat nouvelEtat);
      FilterDTO getFilters();
    Page<Sujet> listSujetsCreatedByTureurs(Pageable pageable);

    Page<Sujet> getSujetsByFilters(
            List<String> thematiques,
            List<Integer> annees,
            List<String> societes,
            List<String> specialites,
            List<Etat> etats,
            Pageable pageable);
    public FilterTutorDTO getFilterCriteria();
    Page<Sujet> filterSujetsCreatedByAllTuteurs(List<String> thematiques,
                             List<Integer> annees,
                             List<String> titres,
                             List<String> tuteurs,
                             List<String> specialites,
                             List<Etat> etats,
                             Pageable pageable);

    Page<Sujet> getSujetsAccepteed(Pageable pageable);
    SujetAcceptedFiltersDTO getAllFiltersForAcceptedSujets();

    Page<Sujet> filterAcceptedSujets(List<String> thematiques, List<String> specialites, List<Integer> annees, List<String> titres, Pageable pageable);

    SujetVisibilityResponse rendreSujetsVisibles(Long tuteurId, List<Long> sujetIds);
}
