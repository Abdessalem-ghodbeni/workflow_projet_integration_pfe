package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.CodeAnalysisResultRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITacheRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITuteurRepository;
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
    private final ITuteurRepository tuteurRepository;
    private final ITacheRepository tacheRepository;
    private final IHistoriqueServiceImp historiqueServiceImp;
    private final CodeAnalysisResultRepository codeAnalysisResultRepository;
    private final GitHubIntegrationServiceImpl gitHubService;
    private final CodacyService codacyService;
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



//    @Transactional
//    public CodeAnalysisResult analyserEtEnregistrerMetrics(
//            Long tacheId,
//            String branchName,
//            Long utilisateurId
//    ) {
//        // 1) Charger la tâche
//        Tache tache = tacheRepository.findById(tacheId)
//                .orElseThrow(() -> new RuntimeException("Tâche introuvable"));
//
//        // 2) Charger le tuteur et extraire son token
//        Tuteur tuteur =tuteurRepository.findById(utilisateurId)
//                .orElseThrow(() -> new RuntimeException("Tuteur introuvable"));
//        String token = tuteur.getGithubToken();  // déjà déchiffré par le converter
//
//        // 3) Extraire owner et repo depuis le lien GitHub du projet
//        String lien = tache.getBacklog().getProjet().getLienGitHub();
//        String path = lien.replace(".git", "")
//                .substring(lien.indexOf("github.com/") + 11);
//        String[] parts = path.split("/");
//
//
//        if (parts.length < 2) {
//            throw new IllegalArgumentException("URL GitHub invalide : " + lien);
//        }
//        String owner = parts[0], repo = parts[1];
//
//        // 4) Désactiver l’ancienne analyse active
//        tache.getAnalyses().forEach(a -> a.setEstAnalyseActive(false));
//        System.out.println(" ya slouma raw URL utilisée : https://api.github.com/repos/" + owner + "/" + repo + "/pulls");
//        System.out.println("slouma raw Token utilisé : " + token); // juste temporairement pour debug
//
//        // 5) Appeler GitHubIntegrationService pour récupérer toutes les métriques
//        CodeAnalysisResult nouvelleAnalyse = gitHubService.analyzeBranch(
//                owner, repo, branchName, tache, token
//        );
//
//
//        // 6) Marquer cette analyse comme active et persister
//        nouvelleAnalyse.setEstAnalyseActive(true);
//        nouvelleAnalyse.setDateDerniereAnalyseGit(LocalDateTime.now());
//        CodeAnalysisResult saved = codeAnalysisResultRepository.save(nouvelleAnalyse);
//
//
//        // 7) Lier la nouvelle analyse à la tâche et sauvegarder la tâche
//        tache.getAnalyses().add(saved);
//        tacheRepository.save(tache);
//
//
//
//        // 8) Historique
//        historiqueServiceImp.enregistrerAction(
//                utilisateurId,
//                "ANALYSE_GITHUB",
//                tuteur.getNom() + " a lancé l'analyse GitHub sur '"
//                        + branchName + "' pour la tâche \""
//                        + tache.getTitre() + "\""
//        );
//
//        return saved;
//    }

@Transactional
public CodeAnalysisResult analyserEtEnregistrerMetrics(
        Long tacheId,
        String branchName,
        Long utilisateurId
) {
    // 1) Charger la tâche
    Tache tache = tacheRepository.findById(tacheId)
            .orElseThrow(() -> new RuntimeException("Tâche introuvable"));

    // 2) Récupérer le token GitHub du tuteur
    Tuteur tuteur = tuteurRepository.findById(utilisateurId)
            .orElseThrow(() -> new RuntimeException("Tuteur introuvable"));
    String token = tuteur.getGithubToken();

    // 3) Extraire owner et repo depuis le lien GitHub
    String lien = tache.getBacklog().getProjet().getLienGitHub();
    String path = lien.replace(".git", "")
            .substring(lien.indexOf("github.com/") + 11);
    String[] parts = path.split("/");
    if (parts.length < 2) {
        throw new IllegalArgumentException("URL GitHub invalide : " + lien);
    }
    String owner = parts[0], repo = parts[1];

    // 4) Désactiver l’ancienne analyse active
    tache.getAnalyses().forEach(a -> a.setEstAnalyseActive(false));

    // 5) Appeler GitHubIntegrationService pour récupérer les métriques GitHub
    CodeAnalysisResult nouvelleAnalyse = gitHubService.analyzeBranch(
            owner, repo, branchName, tache, token
    );

    // 6) Appeler Codacy et fusionner les résultats
    try {
        CodacyAnalysisResponse codacyResp = codacyService
                .analyserBranche(lien, branchName)
                .block(); // on bloque pour récupérer l’objet

        if (codacyResp != null) {
            nouvelleAnalyse.setCodeSmells(codacyResp.getCode_smells());
            nouvelleAnalyse.setBugsDetectes(codacyResp.getBugs());
            nouvelleAnalyse.setDuplications(codacyResp.getDuplications());
            nouvelleAnalyse.setCouvertureTests(codacyResp.getCoverage());
        }
    } catch (Exception e) {
        log.error("Erreur lors de l'appel à Codacy : {}", e.getMessage());
        // Vous pouvez décider de lever l'exception ou de continuer sans Codacy
    }

    // 7) Marquer comme active et persister
    nouvelleAnalyse.setEstAnalyseActive(true);
    nouvelleAnalyse.setDateDerniereAnalyseGit(LocalDateTime.now());
    CodeAnalysisResult saved = codeAnalysisResultRepository.save(nouvelleAnalyse);

    // 8) Lier à la tâche
    tache.getAnalyses().add(saved);
    tacheRepository.save(tache);

    // 9) Historique
    historiqueServiceImp.enregistrerAction(
            utilisateurId,
            "ANALYSE_GLOBALE",
            tuteur.getNom() + " a lancé l'analyse GitHub+Codacy sur '"
                    + branchName + "' pour la tâche \"" + tache.getTitre() + "\""
    );

    return saved;
}



}
