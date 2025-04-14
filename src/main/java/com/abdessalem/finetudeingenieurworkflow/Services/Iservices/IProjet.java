package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Projet;
import com.abdessalem.finetudeingenieurworkflow.Entites.ProjetRequest;

import java.util.List;

public interface IProjet {
    Projet ajouterProjet(ProjetRequest projet);
    Projet supprimerProjet(Long id);
    Projet modifierUnProjet(Projet projet);
    List<Projet> recupererProjet();
    Projet recupererUnProjet(Long id);

    //nouvelle version
    ApiResponse affecterSujetAEquipe(String titreSujet, Long equipeId,Long tuteurId);
    ApiResponse desaffecterSujetAEquipe(String titreSujet, Long equipeId,Long tuteurId);
    ApiResponse updateLienGitHub(Long projetId, Long etudiantid,String nouveauLienGitHub);
    Projet getProjetByEtudiantId(Long etudiantId);
    Projet getProjetById(Long id);
}
