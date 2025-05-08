package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.EtatTache;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import com.abdessalem.finetudeingenieurworkflow.Entites.TacheRequest;

import java.util.List;

public interface ITacheServices {
    ApiResponse ajouterTache(TacheRequest request,Long idEtudiant);
    ApiResponse modifierTache(Long tacheId, TacheRequest request, Long idEtudiant);
    ApiResponse supprimerTache(Long tacheId, Long etudiantId);
    Tache getTacheById(Long tacheId);
    List<Tache> getTachesByProjetId(Long projetId);
    ApiResponse affecterTacheAEtudiant(Long idTache, Long idEtudiantCible, Long idUserQuiAffecte);
    ApiResponse changerEtatTache(Long idTache, EtatTache nouvelEtat, Long idEtudiant);

}
