package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Sprint;

import java.util.List;

public interface ISprintServices {
    ApiResponse ajouterSprint(Sprint request, Long projetId, Long etudiantId);
    ApiResponse modifierSprint(Long sprintId, Sprint request, Long etudiantId);
    List<Sprint> getSprintsByProjetId(Long projetId);
    ApiResponse supprimerSprint(Long sprintId, Long idEtudiant);

    ApiResponse affecterTacheAuSprint(Long idTache, Long idSprint, Long idEtudiant);
//    ApiResponse deplacerTacheVersSprint(Long idTache, Long idSprint, Long idEtudiant);
    ApiResponse desaffecterTacheDuSprint(Long idTache, Long idEtudiant);
    Sprint GetSprintById(Long idSprint);
}
