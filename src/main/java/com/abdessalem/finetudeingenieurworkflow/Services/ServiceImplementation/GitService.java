package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import java.util.Map;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitService {
    private static final Logger logger = Logger.getLogger(GitService.class.getName());
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${git.repo.base-path}")
    private String basePath;

//    public void cloneProject(String projectUrl, String branchName, Long taskId, String githubToken)
//            throws IOException, GitAPIException {
//
//        //  Construire le chemin du dossier
//        Path repoPath = Paths.get(basePath, taskId + "_" + branchName);
//
//        //   Supprimer le dossier s'il existe déjà
//        if (Files.exists(repoPath)) {
//            deleteDirectory(repoPath.toFile());
//        }
//
//        Files.createDirectories(repoPath);
//
//       //debuggggerrrr rani fadittt
//        System.setProperty("org.eclipse.jgit.util.FS.debugTrace", "true");
//        System.setProperty("org.eclipse.jgit.transport.http.HttpTransport.debugTrace", "true");
//
//        // x-access-token 5ater jed bou git  secure za3ma za3ma dima lezmni n5alih huwa fil user name
//
//        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("x-access-token", githubToken);
//
//        // 5. Cloner la branche spécifiée avec le provider
//        try (Git git = Git.cloneRepository()
//                .setURI(projectUrl)  // Utiliser l'URL normale (sans token dans l'URL)
//                .setDirectory(repoPath.toFile())
//                .setBranch(branchName)
//                .setCredentialsProvider(credentialsProvider)  // Ajout du provider
//                .call()) {
//
//            logger.info("Projet cloné avec succès dans : " + repoPath);
//        }
//    }
public Path cloneProject(String projectUrl, String branchName, Long taskId, String githubToken)
        throws IOException, GitAPIException {

    Path repoPath = Paths.get(basePath, taskId + "_" + branchName);

    if (Files.exists(repoPath)) {
        deleteDirectory(repoPath.toFile());
    }
    Files.createDirectories(repoPath);

    CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("x-access-token", githubToken);

    try (Git git = Git.cloneRepository()
            .setURI(projectUrl)
            .setDirectory(repoPath.toFile())
            .setBranch(branchName)
            .setCredentialsProvider(credentialsProvider)
            .call()) {

        logger.info("Projet cloné avec succès dans : " + repoPath);
    }

    return repoPath; // Retourner le chemin du projet cloné
}


    public void analyzeProject(String projectPath) {
        String flaskUrl = "http://localhost:5000/analyze";

        Map<String, String> requestBody = Map.of("project_path", projectPath);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody);

        ResponseEntity<String> response = restTemplate.exchange(flaskUrl, HttpMethod.POST, request, String.class);
        log.info("Réponse Flask : " + response.getBody());
    }
    /**
     * Supprime un dossier et son contenu de manière récursive
     * @param folderToDelete Dossier à supprimer
     * @throws java.io.IOException Si la suppression échoue
     */
    private void deleteDirectory(File folderToDelete) throws IOException {
        if (folderToDelete.isDirectory()) {
            for (File file : folderToDelete.listFiles()) {
                deleteDirectory(file);
            }
        }
        if (!folderToDelete.delete()) {
            throw new IOException("Impossible de supprimer le dossier : " + folderToDelete.getAbsolutePath());
        }
    }
}
