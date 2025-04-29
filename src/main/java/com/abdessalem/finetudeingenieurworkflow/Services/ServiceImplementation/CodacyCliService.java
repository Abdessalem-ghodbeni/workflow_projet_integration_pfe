package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.CodeAnalysisResult;
import com.abdessalem.finetudeingenieurworkflow.Repository.CodeAnalysisResultRepository;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class CodacyCliService {
    private final CodeAnalysisResultRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // Répertoire local pour installer le CLI
    private static final Path CLI_DIR = Paths.get(System.getProperty("user.home"), ".codacy");
    private static final Path CLI_PATH = CLI_DIR.resolve("codacy-analysis-cli");

    /**
     * Assure que le binaire Codacy CLI est disponible: télécharge et installe si nécessaire.
     */
    private String getLatestVersion() throws IOException {
        Process process = new ProcessBuilder("curl", "-s",
                "https://api.github.com/repos/codacy/codacy-analysis-cli/releases/latest")
                .start();

        JsonNode response = new ObjectMapper().readTree(process.getInputStream());
        return response.get("tag_name").asText().replace("v", ""); // Retourne "14.0.0"
    }

    private String getOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux")) return "linux";
        if (osName.contains("mac")) return "darwin";
        if (osName.contains("win")) return "windows";
        throw new UnsupportedOperationException("OS non supporté: " + osName);
    }

    private String getArchitecture() {
        String arch = System.getProperty("os.arch").toLowerCase();
        if (arch.contains("64")) return "amd64";
        if (arch.contains("arm")) return "arm64";
        throw new UnsupportedOperationException("Architecture non supportée: " + arch);
    }
    private void ensureCliInstalled() throws IOException, InterruptedException {
        if (Files.notExists(CLI_PATH)) {
            Files.createDirectories(CLI_DIR);

            // 1. Configuration vérifiée manuellement
            String version = "14.0.0";
            String os = "linux";  // Forçage pour WSL
            String arch = "amd64"; // Architecture x86_64

            // 2. URL corrigée avec le bon format
            String url = String.format(
                    "https://github.com/codacy/codacy-analysis-cli/releases/download/%s/codacy-analysis-cli_%s_%s_%s.tar.gz",
                    version, version, os, arch
            );

            System.out.println("URL de téléchargement validée : " + url);

            Path tarPath = CLI_DIR.resolve("codacy-cli.tar.gz");

            // 3. Téléchargement avec vérification
            Process download = new ProcessBuilder("curl", "-fL", url, "-o", tarPath.toString())
                    .inheritIO()
                    .start();

            int exitCode = download.waitFor();
            if (exitCode != 0) {
                throw new IOException("ERREUR: Téléchargement échoué (code " + exitCode + ")\n"
                        + "Vérifiez manuellement: " + url
                        + "\nSi ça ne fonctionne pas, essayez avec une version antérieure: 13.21.0");
            }


            // 4. Téléchargement avec vérification
            Process downloadProcess = new ProcessBuilder("curl", "-fL", url, "-o", tarPath.toString())
                    .inheritIO()
                    .start();

            if (download.waitFor() != 0) {
                throw new IOException("URL corrigée : " + url.replace(version, "v" + version));
            }
            // 5. Extraction avec vérification en deux étapes
            Process extract = new ProcessBuilder("tar", "xzf", tarPath.toString(), "-C", CLI_DIR.toString())
                    .inheritIO()
                    .start();

            if (extract.waitFor() != 0) {
                throw new IOException("Échec extraction (code sortie: " + extract.exitValue() + ")");
            }

            // 6. Nettoyage et vérification finale
            Files.delete(tarPath);
            if (!Files.exists(CLI_PATH)) {
                throw new FileNotFoundException("Fichier binaire non trouvé après extraction");
            }

            // 7. Définition des permissions POSIX explicites
            Files.setPosixFilePermissions(CLI_PATH, Set.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_EXECUTE
            ));
        }
    }
    private String resolveOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux")) return "linux";
        if (osName.contains("mac")) return "darwin";
        if (osName.contains("win")) return "windows";
        throw new UnsupportedOperationException("OS non supporté: " + osName);
    }

    private String resolveArchitecture() {
        String arch = System.getProperty("os.arch").toLowerCase();
        if (arch.contains("amd64") || arch.contains("x86_64")) return "amd64";
        if (arch.contains("aarch64") || arch.contains("arm64")) return "arm64";
        if (arch.contains("x86")) return "386";
        throw new UnsupportedOperationException("Architecture non supportée: " + arch);
    }
//    private void ensureCliInstalled() throws IOException, InterruptedException {
//        if (Files.notExists(CLI_PATH)) {
//            Files.createDirectories(CLI_DIR);
//            // Télécharger et installer le CLI dans CLI_DIR
//            String installCmd = String.format(
//                    "curl -Ls https://coverage.codacy.com/get.sh | bash -s -- -b %s",
//                    CLI_DIR.toString()
//            );
////            Process p = new ProcessBuilder("bash", "-c", installCmd)
////                    .inheritIO()
////                    .start();
//            Process p = new ProcessBuilder("sh", "-c", installCmd)
//                    .inheritIO()
//                    .start();
//            if (p.waitFor() != 0) {
//                throw new IllegalStateException("Échec de l'installation du Codacy CLI");
//            }
//            // Donner la permission d'exécution
//            CLI_PATH.toFile().setExecutable(true);
//        }
//    }

    /**
     * Clone le repo + branche, lance Codacy CLI, parse et stocke le résultat.
     */
    public CodeAnalysisResult analyzeAndSave(String repoUrl, String branch) throws Exception {
        // S'assurer du CLI
        ensureCliInstalled();

        // 1) Création d'un dossier temporaire
        Path tmp = Files.createTempDirectory("repo-" + UUID.randomUUID());
        try {
            // 2) Clonage de la branche
            Process clone = new ProcessBuilder(
                    "git", "clone", "--branch", branch,
                    "--single-branch", repoUrl, tmp.toString()
            ).inheritIO().start();
            if (clone.waitFor() != 0) {
                throw new IllegalStateException("Échec du git clone");
            }

            // 3) Exécution de Codacy CLI local
            Path report = tmp.resolve("codacy-report.json");
            Process cli = new ProcessBuilder(
                    CLI_PATH.toString(), "analyze",
                    "--directory", tmp.toString(),
                    "--output", report.toString()
            ).inheritIO().start();
            if (cli.waitFor() != 0) {
                throw new IllegalStateException("Échec du Codacy CLI");
            }

            // 4) Parsing du JSON
            JsonNode root = objectMapper.readTree(report.toFile());
            JsonNode metrics = root.path("metrics");
            ArrayNode issues = (ArrayNode) root.path("issues");

            // 5) Compter bugs et code smells
            int bugsDetected = 0;
            int codeSmells   = 0;
            for (JsonNode issue : issues) {
                String severity = issue.path("severity").asText();
                if ("error".equalsIgnoreCase(severity)) {
                    bugsDetected++;
                } else if ("warning".equalsIgnoreCase(severity)) {
                    codeSmells++;
                }
            }

            // 6) Construction de l'entité
            CodeAnalysisResult result = CodeAnalysisResult.builder()
                    .nomBrancheGit(branch)
                    .scoreQualiteCode(100.0 - bugsDetected)
                    .couvertureTests(metrics.path("coverage").asDouble(0.0))
                    .duplications(metrics.path("duplication").asInt(0))
                    .bugsDetectes(bugsDetected)
                    .codeSmells(codeSmells)
                    .estAnalyseActive(true)
                    .dateDerniereAnalyseGit(LocalDateTime.now())
                    .build();

            // 7) Sauvegarde
            return repository.save(result);

        } finally {
            // 8) Nettoyage
            FileUtils.deleteDirectory(tmp.toFile());
        }
    }
}
