package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.DtoGithub.CommitDetailDto;
import com.abdessalem.finetudeingenieurworkflow.DtoGithub.CommitSummaryDto;
import com.abdessalem.finetudeingenieurworkflow.DtoGithub.PullRequestDto;
import com.abdessalem.finetudeingenieurworkflow.Entites.CodeAnalysisResult;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import com.abdessalem.finetudeingenieurworkflow.Repository.CodeAnalysisResultRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubIntegrationServiceImpl {
    private final WebClient webClient;
    private final CodeAnalysisResultRepository analysisRepo;
    @Value("${github.api.base-url}")
    private String baseUrl;

    @Value("${github.http.retry-attempts}")
    private int retryAttempts;

    @Value("${github.http.retry-backoff-seconds}")
    private int backoffSeconds;

    private String getDefaultBranch(String owner, String repo, String token) {
        JsonNode repoInfo = webClient.get()
                .uri(baseUrl + "/repos/{owner}/{repo}", owner, repo)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return repoInfo.get("default_branch").asText();
    }
    private List<String> fetchCommitsForBranch(
            String owner,
            String repo,
            String branch,
            String token
    ) {
        // 1) Vérifier si la branche est mergée
        boolean merged = isBranchMerged(owner, repo, branch, token);

        if (merged) {
            // 2a) Si mergée → lister les commits de la PR
            return fetchCommitsFromPullRequest(owner, repo, branch, token);
        } else {
            // 2b) Si pas mergée → comparer avec la branche principale
            String base = getDefaultBranch(owner, repo, token);
            return fetchCommitsFromCompare(owner, repo, base, branch, token);
        }
    }
    private List<String> fetchCommitsFromCompare(
            String owner,
            String repo,
            String baseBranch,
            String headBranch,
            String token
    ) {
        JsonNode compare = webClient.get()
                .uri(baseUrl + "/repos/{owner}/{repo}/compare/{base}...{head}",
                        owner, repo, baseBranch, headBranch)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        // Si tu préfères le champ "ahead_by":
        // int total = compare.get("ahead_by").asInt();

        // Retourner la liste des commits
        List<String> shas = new ArrayList<>();
        compare.get("commits").forEach(c -> shas.add(c.get("sha").asText()));
        return shas;
    }

    private List<String> fetchCommitsFromPullRequest(
            String owner,
            String repo,
            String branch,
            String token
    ) {
        // 1) Trouver la PR pour la branche
        List<PullRequestDto> prs = webClient.get()
                .uri(baseUrl + "/repos/{owner}/{repo}/pulls?head={owner}:{branch}&state=all",
                        owner, repo, owner, branch)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToFlux(PullRequestDto.class)
                .collectList()
                .block();

        if (prs.isEmpty()) {
            throw new RuntimeException("Aucune PR trouvée pour la branche : " + branch);
        }
        int prNumber = prs.get(0).getNumber();

        // 2) Récupérer les commits de la PR
        List<CommitSummaryDto> commits = webClient.get()
                .uri(baseUrl + "/repos/{owner}/{repo}/pulls/{pr}/commits",
                        owner, repo, prNumber)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToFlux(CommitSummaryDto.class)
                .collectList()
                .block();

        return commits.stream()
                .map(CommitSummaryDto::getSha)
                .collect(Collectors.toList());
    }

public CodeAnalysisResult analyzeBranch(
        String owner,
        String repo,
        String branch,
        Tache tache,
        String githubToken
) {
    // Récupère dynamiquement tous les commits pertinents
    List<String> shas = fetchCommitsForBranch(owner, repo, branch, githubToken);

    // Puis le reste de ta logique inchangé :
    List<CommitDetailDto> details = shas.stream()
            .map(sha -> fetchCommitDetail(owner, repo, sha, githubToken))
            .collect(Collectors.toList());

    int totalCommits = details.size();
    int additions   = details.stream().mapToInt(d -> d.getStats().getAdditions()).sum();
    int deletions   = details.stream().mapToInt(d -> d.getStats().getDeletions()).sum();
    double consistency = calculateConsistency(details);

    CodeAnalysisResult result = CodeAnalysisResult.builder()
            .nomBrancheGit(branch)
            .brancheMergee(isBranchMerged(owner, repo, branch, githubToken))
            .nombreCommits(totalCommits)
            .lignesCodeAjoutees(additions)
            .lignesCodeSupprimees(deletions)
            .scoreConsistanceCommits(consistency)
            .estAnalyseActive(true)
            .dateDerniereAnalyseGit(LocalDateTime.now())
            .tache(tache)
            .build();

    return analysisRepo.save(result);
}


    private boolean isBranchMerged(
        String owner, String repo, String branch, String token
) {
    PullRequestDto pr = webClient.get()
            .uri(baseUrl + "/repos/{owner}/{repo}/pulls?head={owner}:{branch}&state=all",
                    owner, repo, owner, branch)
            .headers(h -> h.setBearerAuth(token))
            .retrieve()
            .bodyToFlux(PullRequestDto.class)
            .retryWhen(backoffSpec())
            .blockFirst(Duration.ofSeconds(10));  // peut être null si aucune PR

    return pr != null && pr.getMergedAt() != null;
}




private List<String> fetchAllCommitShas(String owner, String repo, String branch, String token) {
    List<String> shas = new ArrayList<>();
//    String url = baseUrl + "/repos/{owner}/{repo}/commits?sha={branch}&per_page=100";
    String url = baseUrl + "/repos/{owner}/{repo}/commits?sha={branch}&per_page=100&exclude_pull_requests=true";
    String next = UriComponentsBuilder.fromUriString(url)
            .build(owner, repo, branch)
            .toString();

    do {
        try {
            ClientResponse resp = webClient.get()
                    .uri(next)
                    .headers(h -> h.setBearerAuth(token))
                    .exchange()
                    .retryWhen(backoffSpec())
                    .block();

            if (resp != null && resp.statusCode().is2xxSuccessful()) {
                List<CommitSummaryDto> page = resp.bodyToFlux(CommitSummaryDto.class)
                        .collectList().block();
                if (page != null) {
                    shas.addAll(page.stream().map(CommitSummaryDto::getSha).toList());
                }

                next = resp.headers().asHttpHeaders()
                        .getFirst(HttpHeaders.LINK);
                if (next != null && next.contains("rel=\"next\"")) {
                    next = extractNextLink(next);
                } else {
                    next = null;
                }
            } else {
                log.error("Failed to fetch commits: {}", resp.statusCode());
                next = null;
            }
        } catch (WebClientRequestException | WebClientResponseException e) {
            log.error("Error fetching commits: {}", e.getMessage());
            next = null;
        }
    } while (next != null);

    return shas;
}


    private CommitDetailDto fetchCommitDetail(
            String owner, String repo, String sha, String token
    ) {
        return webClient.get()
                .uri(baseUrl + "/repos/{owner}/{repo}/commits/{sha}", owner, repo, sha)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(CommitDetailDto.class)
                .retryWhen(backoffSpec())
                .block();
    }

    private double calculateConsistency(List<CommitDetailDto> details) {
        if (details.isEmpty()) return 0;
        long good = details.stream()
                .map(d -> d.getCommit().getMessage())
                .filter(msg -> msg.matches("^(feat|fix|docs|style|refactor|test|chore)(\\(.+\\))?: .+"))
                .count();
        return (double) good / details.size();
    }

    private Retry backoffSpec() {
        return Retry.backoff(retryAttempts, Duration.ofSeconds(backoffSeconds))
                .filter(throwable ->
                        throwable instanceof WebClientRequestException ||
                                (throwable instanceof WebClientResponseException resp &&
                                        resp.getStatusCode().is5xxServerError())
                );
    }

    private String extractNextLink(String linkHeader) {
        for (String part : linkHeader.split(",")) {
            if (part.contains("rel=\"next\"")) {
                return part.substring(
                        part.indexOf('<') + 1, part.indexOf('>')
                );
            }
        }
        return null;
    }



}
