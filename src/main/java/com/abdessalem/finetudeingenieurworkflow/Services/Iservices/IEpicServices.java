package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;

public interface IEpicServices {
    ApiResponse addEpicToProject(Long projetId, Long etudiantId, Epic epic);
}
