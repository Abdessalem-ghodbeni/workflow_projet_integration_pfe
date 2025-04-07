package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import com.abdessalem.finetudeingenieurworkflow.Entites.TacheRequest;

public interface ITacheServices {
    ApiResponse ajouterTache(TacheRequest request,Long idEtudiant);
    ApiResponse modifierTache(Long tacheId, TacheRequest request, Long idEtudiant);
    ApiResponse supprimerTache(Long tacheId, Long etudiantId);
    Tache getTacheById(Long tacheId);
}
