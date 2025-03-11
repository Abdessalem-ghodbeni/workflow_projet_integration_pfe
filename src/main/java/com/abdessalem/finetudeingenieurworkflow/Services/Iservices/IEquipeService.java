package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Equipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.EtatEquipe;

import java.util.List;

public interface IEquipeService {

    ApiResponse construireEquipes(Long formId);
    ApiResponse ajouterEtudiantAEquipe(Long etudiantId, Long equipeId,Long userId);
    ApiResponse retirerEtudiantDeEquipe(Long userId, Long etudiantId);
    List<Equipe> getEquipesBySpecialiteAndCurrentYear(String specialite);
    ApiResponse changerStatutEquipe(Long equipeId, EtatEquipe nouveauStatut);
}
