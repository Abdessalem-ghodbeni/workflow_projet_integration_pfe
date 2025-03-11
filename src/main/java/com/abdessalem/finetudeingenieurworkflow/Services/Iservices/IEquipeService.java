package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;

public interface IEquipeService {

    ApiResponse construireEquipes(Long formId);
    ApiResponse ajouterEtudiantAEquipe(Long etudiantId, Long equipeId,Long userId);
    ApiResponse retirerEtudiantDeEquipe(Long userId, Long etudiantId);
}
