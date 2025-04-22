package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.CodeAnalysisResultRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITacheRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ICodeAnalysisResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeAnalysisResultServicesImpl implements ICodeAnalysisResultService {
    private final IUserRepository userRepository;
    private final ITacheRepository tacheRepository;
    private final IHistoriqueServiceImp historiqueServiceImp;
    private final CodeAnalysisResultRepository codeAnalysisResultRepository;
    @Override
    @Transactional
    public ApiResponse initierAnalyseCode(Long tacheId, String nomBrancheGit, Long utilisateurId) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche introuvable"));

        User utilisateur = userRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // ndesactiveha bech kol man3mil jed bou analyse jdid nhotha true w njbed 9dima ,wa9tch wehd ye5dem w yhej min hal bled
        tache.getAnalyses().forEach(a -> a.setEstAnalyseActive(false));

        // Créer une nouvelle analyse
        CodeAnalysisResult analyse = CodeAnalysisResult.builder()
                .nomBrancheGit(nomBrancheGit)
                .estAnalyseActive(true)
                .dateDerniereAnalyseGit(LocalDateTime.now())
                .tache(tache)
                .build();

        codeAnalysisResultRepository.save(analyse);

        // Ajouter l’analyse à la tâche
        tache.getAnalyses().add(analyse);
        tacheRepository.save(tache);
        historiqueServiceImp.enregistrerAction(utilisateurId, "MODIFICATION",
                utilisateur.getNom() + " a ajouté nomBranch au tache " + tache.getTitre());


        return new ApiResponse( "Analyse initialisée avec succès pour la branche : ",true);
    }

    @Override
    @Transactional
    public ApiResponse modifierNomBrancheGitAnalyseActive(Long tacheId, String nouveauNom, Long utilisateurId) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche introuvable"));

        User utilisateur = userRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        CodeAnalysisResult analyseActive = tache.getAnalyses().stream()
                .filter(CodeAnalysisResult::isEstAnalyseActive)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aucune analyse active trouvée pour cette tâche."));

        String ancienNom = analyseActive.getNomBrancheGit();
        analyseActive.setNomBrancheGit(nouveauNom);

        codeAnalysisResultRepository.save(analyseActive);

        historiqueServiceImp.enregistrerAction(
                utilisateurId,
                "MODIFICATION",
                utilisateur.getNom() + " a modifié le nom de la branche Git de '" + ancienNom + "' vers '" + nouveauNom + "' pour la tâche '" + tache.getTitre() + "'"
        );

        return new ApiResponse("Nom de branche Git modifié avec succès.", true);
    }
}
