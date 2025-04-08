package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;

import java.util.List;

public interface IEpicServices {
    ApiResponse addEpicToProject(Long projetId, Long etudiantId, Epic epic);
    ApiResponse updateEpic(Long epicId, Long etudiantId, Epic epic);
    Epic getEpicById(Long epicId);
    ApiResponse deleteEpic(Long epicId, Long etudiantId);
    List<Epic> getEpicsByProjetId(Long projetId);
}
