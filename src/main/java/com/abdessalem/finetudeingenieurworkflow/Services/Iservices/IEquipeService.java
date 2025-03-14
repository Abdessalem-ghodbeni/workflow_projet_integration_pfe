package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Equipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.EtatEquipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;

import java.util.List;

public interface IEquipeService {

    ApiResponse construireEquipes(Long formId);
    ApiResponse ajouterEtudiantAEquipe(Long etudiantId, Long equipeId,Long userId);
    ApiResponse retirerEtudiantDeEquipe(Long userId, Long etudiantId);
    List<Etudiant> getEquipesBySpecialiteAndCurrentYear(String specialite);
    ApiResponse changerStatutEquipe(Long equipeId,Long tuteurId, EtatEquipe nouveauStatut);
    List<Equipe> getEquipesByYearAndSpecialite(String specialite);
    Equipe getEquipeByEtudiantId(Long etudiantId);
    List<Equipe> getEquipesByIds(List<Long> ids);
}
