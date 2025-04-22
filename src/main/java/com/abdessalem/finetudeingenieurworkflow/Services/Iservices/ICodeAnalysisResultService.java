package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;

public interface ICodeAnalysisResultService {
    ApiResponse initierAnalyseCode(Long tacheId, String nomBrancheGit, Long utilisateurId);
}
