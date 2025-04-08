package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Sprint;

public interface ISprintServices {
    ApiResponse ajouterSprint(Sprint request, Long projetId, Long etudiantId);
    ApiResponse modifierSprint(Long sprintId, Sprint request, Long etudiantId);
}
