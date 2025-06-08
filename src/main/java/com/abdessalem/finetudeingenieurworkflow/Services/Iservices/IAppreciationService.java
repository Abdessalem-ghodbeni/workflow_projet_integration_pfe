package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.Appreciation;
import com.abdessalem.finetudeingenieurworkflow.Entites.AppreciationDTO;

import java.util.List;

public interface IAppreciationService {
    Appreciation createAppreciation(AppreciationDTO dto);
    List<Appreciation> listerParEtudiant(Long etudiantId);
    Appreciation updateAppreciation(Long id, AppreciationDTO dto);
}
