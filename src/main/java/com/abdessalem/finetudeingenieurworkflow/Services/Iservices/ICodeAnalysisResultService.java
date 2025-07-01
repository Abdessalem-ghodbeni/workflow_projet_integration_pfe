package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.CodeAnalysisResult;

import java.util.List;

public interface ICodeAnalysisResultService {
    ApiResponse initierAnalyseCode(Long tacheId, String nomBrancheGit, Long utilisateurId);
    ApiResponse modifierNomBrancheGitAnalyseActive(Long tacheId, String nouveauNom, Long utilisateurId);
    List<CodeAnalysisResult>FetchAllAnalyseByIdTache(Long idTache);
}
