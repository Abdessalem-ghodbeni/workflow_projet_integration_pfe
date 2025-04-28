package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.CodacyAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodacyService {
    @Value("${codacy.api.token}")
    private String apiToken;

    @Value("${codacy.api.url}")
    private String codacyApiUrl;

    private final WebClient.Builder webClientBuilder;

//    public Mono<CodacyAnalysisResponse> analyserBranche(String repoUrl, String branch) {
//        String endpoint = codacyApiUrl + "/repositories/remote/" + encodeUrl(repoUrl) + "/branch/" + branch + "/metrics";
//
//        return webClientBuilder.build()
//                .get()
//                .uri(endpoint)
//                .header("api-token", apiToken)
//                .retrieve()
//                .bodyToMono(CodacyAnalysisResponse.class);
//    }
// Dans CodacyService.java
//public Mono<CodacyAnalysisResponse> analyserBranche(String repoUrl, String branch) {
//    String encodedRepo = encodeUrl(repoUrl);
//    String endpoint = String.format("%s/repositories/remote/%s/branch/%s/metrics",
//            codacyApiUrl, encodedRepo, branch);
//
//    return webClientBuilder.build()
//            .get()
//            .uri(URI.create(endpoint)) // Désactive l'encodage automatique
//            .header("api-token", apiToken)
//            .retrieve()
//            .onStatus(HttpStatusCode::isError, response ->
//                    response.bodyToMono(String.class)
//                            .flatMap(body -> Mono.error(new RuntimeException(
//                                    "Erreur Codacy " + response.statusCode() + " : " + body)))
//            )
//            .bodyToMono(CodacyAnalysisResponse.class)
//            .retry(3);
//}
public Mono<CodacyAnalysisResponse> analyserBranche(String repoUrl, String branch) {
    String cleanedUrl = repoUrl.replace("https://", "").replace(".git", "");
    int idx = cleanedUrl.indexOf("github.com/");
    if (idx == -1) {
        throw new IllegalArgumentException("URL GitHub invalide : " + repoUrl);
    }
    // Extrait "owner/repo" puis encode "owner%2Frepo"
    String ownerRepo = cleanedUrl.substring(idx + "github.com/".length())
            .replace("/", "%2F");

    String provider = "github";
    // On passe en pluriel "branches" et on réinsère "remote"
    String endpoint = String.format(
            "%s/repositories/remote/%s/%s/branches/%s/metrics",
            codacyApiUrl,
            provider,
            ownerRepo,
            branch
    );

    log.debug("Codacy endpoint → {}", endpoint);

    return webClientBuilder.build()
            .get()
            .uri(URI.create(endpoint))
            .header("api-token", apiToken)
            .retrieve()
            .onStatus(HttpStatusCode::isError, response ->
                    response.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new RuntimeException(
                                    "Erreur Codacy " + response.statusCode() + " : " + body)))
            )
            .bodyToMono(CodacyAnalysisResponse.class)
            .retry(3);
}

//    private String encodeUrl(String url) {
//        return url.replace("https://", "").replace("/", "%2F");
//    }
private String encodeUrl(String url) {
    String cleanedUrl = url.replace("https://", "").replace(".git", "");
    int index = cleanedUrl.indexOf("github.com/");
    if (index == -1) {
        throw new IllegalArgumentException("URL GitHub invalide : " + url);
    }
    String path = cleanedUrl.substring(index + "github.com/".length());
    return path.replace("/", "%2F");
}
}
