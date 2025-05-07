package com.abdessalem.finetudeingenieurworkflow.Controller;

import com.abdessalem.finetudeingenieurworkflow.Repository.ITacheRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITuteurRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation.GitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/git")
@RequiredArgsConstructor
@Slf4j
public class GitCloneController {
    private final GitService gitService;
    private final ITacheRepository tacheRepository;
    private final ITuteurRepository tuteurRepository;


    /**
     * Endpoint pour cloner un projet
     * @param taskId ID de la tâche
     * @param branchName Nom de la branche
     * @param tutorId ID du tuteur (pour récupérer le token GitHub)
     * @return Réponse HTTP
     * @throws Exception Si une erreur survient
     */
    @PostMapping("/clone")
    public ResponseEntity<String> cloneProject(
            @RequestParam Long taskId,
            @RequestParam String branchName,
            @RequestParam Long tutorId) throws Exception {

        var tuteur = tuteurRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé"));
        var tache = tacheRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        String projectUrl = tache.getBacklog().getProjet().getLienGitHub();
        Path projectPath=  gitService.cloneProject(projectUrl, branchName, taskId, tuteur.getGithubToken());
        gitService.analyzeProject(projectPath.toString());

        return ResponseEntity.ok("Projet cloné avec succès !");
    }
}
