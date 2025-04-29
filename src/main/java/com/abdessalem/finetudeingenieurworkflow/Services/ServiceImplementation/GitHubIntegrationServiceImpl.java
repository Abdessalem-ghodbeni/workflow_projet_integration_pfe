package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.DtoGithub.CommitDetailDto;
import com.abdessalem.finetudeingenieurworkflow.DtoGithub.CommitSummaryDto;
import com.abdessalem.finetudeingenieurworkflow.DtoGithub.PullRequestDto;
import com.abdessalem.finetudeingenieurworkflow.Entites.CodeAnalysisResult;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import com.abdessalem.finetudeingenieurworkflow.Repository.CodeAnalysisResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        // Récupération des commits
        List<String> shas = fetchCommitsForBranch(owner, repo, branch, githubToken);

        // Extraction des détails des commits
        List<CommitDetailDto> details = shas.stream()
                .map(sha -> fetchCommitDetail(owner, repo, sha, githubToken))
                .collect(Collectors.toList());

        // Calcul des métriques de base
        int totalCommits = details.size();
        int additions = details.stream().mapToInt(d -> d.getStats().getAdditions()).sum();
        int deletions = details.stream().mapToInt(d -> d.getStats().getDeletions()).sum();
        double consistency = enhancedConsistencyCheck(details);

        List<LocalDateTime> commitDates = details.stream()
                .map(d -> {
                    try {
                        return Optional.ofNullable(d.getDetails())
                                .flatMap(CommitDetailDto.CommitDetails::getSafeAuthor)
                                .map(a -> LocalDateTime.parse(
                                        a.getDate(),
                                        DateTimeFormatter.ISO_DATE_TIME
                                ))
                                .orElse(null);
                    } catch (DateTimeParseException e) {
                        log.warn("Format de date invalide pour le commit: {}", d.getSha());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        double avgHoursBetweenCommits = 0;

        if (commitDates.size() > 1) {
            long totalHours = 0;
            for (int i = 1; i < commitDates.size(); i++) {
                totalHours += Duration.between(commitDates.get(i-1), commitDates.get(i)).toHours();
            }
            avgHoursBetweenCommits = (double) totalHours / (commitDates.size() - 1);
        } else {
            avgHoursBetweenCommits = -1.0; // Valeur spéciale "non calculable"
        }

        long lifespanDays = !commitDates.isEmpty() ?
                Duration.between(
                        commitDates.get(0),
                        commitDates.get(commitDates.size() - 1)
                ).toDays() : 0;

        // Détection des conflits de fusion
        boolean isMerged = isBranchMerged(owner, repo, branch, githubToken);
        boolean mergeConflictDetected = false;
        if (isMerged) {
            Integer prNumber = getPrNumberForBranch(owner, repo, branch, githubToken);
            if (prNumber != null) {
                mergeConflictDetected = checkMergeConflicts(owner, repo, prNumber, githubToken);
            }
        }

        // Calcul des moyennes par commit
        double avgAdditions = totalCommits > 0 ? (double) additions / totalCommits : 0;
        double avgDeletions = totalCommits > 0 ? (double) deletions / totalCommits : 0;

        // Analyse des types de commits
        Map<String, Long> commitTypes = analyzeCommitTypes(details);
        String commitTypesJson = "{}";
        try {
            commitTypesJson = new ObjectMapper().writeValueAsString(commitTypes);
        } catch (JsonProcessingException e) {
            log.error("Erreur de sérialisation des types de commits", e);
        }

        // Construction du résultat final
        CodeAnalysisResult result = CodeAnalysisResult.builder()
                .nomBrancheGit(branch)
                .brancheMergee(isMerged)
                .nombreCommits(totalCommits)
                .lignesCodeAjoutees(additions)
                .lignesCodeSupprimees(deletions)
                .scoreConsistanceCommits(consistency)
                .averageHoursBetweenCommits(avgHoursBetweenCommits)
                .branchLifespanDays(lifespanDays)
                .mergeConflictDetected(mergeConflictDetected)
                .averageAdditionsPerCommit(avgAdditions)
                .averageDeletionsPerCommit(avgDeletions)
                .commitTypesDistribution(commitTypesJson)
                .estAnalyseActive(true)
                .dateDerniereAnalyseGit(LocalDateTime.now())
                .tache(tache)
                .build();

        return analysisRepo.save(result);
    }

    // Méthodes helper supplémentaires
    private Integer getPrNumberForBranch(String owner, String repo, String branch, String token) {
        List<PullRequestDto> prs = webClient.get()
                .uri(baseUrl + "/repos/{owner}/{repo}/pulls?head={owner}:{branch}&state=all",
                        owner, repo, owner, branch)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToFlux(PullRequestDto.class)
                .collectList()
                .block();
        return prs.isEmpty() ? null : prs.get(0).getNumber();
    }

    private Map<String, Long> analyzeCommitTypes(List<CommitDetailDto> details) {
        return details.stream()
                .map(d -> extractCommitType(d.getCommit().getMessage()))
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
    }

//    private String extractCommitType(String message) {
//        Matcher matcher = Pattern.compile("^(feat|fix|docs|style|refactor|test|chore)(\\(.*\\))?:")
//                .matcher(message);
//        return matcher.find() ? matcher.group(1) : "other";
//    }
private String extractCommitType(String message) {
    if (message == null) return "no-message";

    Matcher matcher = Pattern.compile(
            "^(build|ci|docs|feat|fix|perf|refactor|style|test|chore|revert|bump)(\\([\\w-]+\\))?:\\s.+"
    ).matcher(message);

    return matcher.find() ? matcher.group(1) : "other";
}
    private double enhancedConsistencyCheck(List<CommitDetailDto> details) {
        long validCommits = details.stream()
                .filter(d -> {
                    String msg = d.getCommit().getMessage();
                    return msg.matches("^(feat|fix|docs|style|refactor|test|chore)(\\(.+\\))?: .{20,}")
                            && msg.split("\n").length >= 2;
                })
                .count();
        return details.isEmpty() ? 0 : (double) validCommits / details.size();
    }
    private boolean checkMergeConflicts(String owner, String repo, int prNumber, String token) {
        PullRequestDto pr = webClient.get()
                .uri(baseUrl + "/repos/{owner}/{repo}/pulls/{pr}", owner, repo, prNumber)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(PullRequestDto.class)
                .block();
        return pr != null && !pr.isMergeable();
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
