package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import com.abdessalem.finetudeingenieurworkflow.Entites.TacheRequest;

public interface ITacheServices {
    ApiResponse ajouterTache(TacheRequest request,Long idEtudiant);
}
