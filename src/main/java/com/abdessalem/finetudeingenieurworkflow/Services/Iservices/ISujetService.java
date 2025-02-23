package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Etat;
import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import org.springframework.data.domain.Page;

import java.util.List;
import org.springframework.data.domain.Pageable;
public interface ISujetService {

Sujet createSujet( Sujet sujet,Long userId);
    Sujet getSujetById(Long id);
    void deleteSujet(Long id);
    List<Sujet> getAllSujets();
    Sujet updateSujet(Sujet sujet);
    Page<Sujet> getSujetsByTuteurId(Long tuteurId, int page);
    Page<Sujet> getSujetsBySocietId(Long societeId, int page);
    Page<Sujet> rechercherSujetParTitre(String titre, Pageable pageable);
    Page<Sujet> getSujetsCreatedBySociete(Pageable pageable);
    ApiResponse changerEtatSujet(Long idSujet, Etat nouvelEtat);


}
