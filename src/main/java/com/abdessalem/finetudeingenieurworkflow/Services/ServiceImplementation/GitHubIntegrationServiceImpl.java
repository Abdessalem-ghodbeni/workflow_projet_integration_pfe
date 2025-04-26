package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.DtoGithub.CommitDetailDto;
import com.abdessalem.finetudeingenieurworkflow.DtoGithub.CommitSummaryDto;
import com.abdessalem.finetudeingenieurworkflow.DtoGithub.PullRequestDto;
import com.abdessalem.finetudeingenieurworkflow.Entites.CodeAnalysisResult;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import com.abdessalem.finetudeingenieurworkflow.Repository.CodeAnalysisResultRepository;
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



    public CodeAnalysisResult analyzeBranch(
            String owner,
            String repo,
            String branch,
            Tache tache,
            String githubToken
    ) {
        // 1. Vérifier le merge
        boolean merged = isBranchMerged(owner, repo, branch, githubToken);

        // 2. Récupérer tous les commits
        List<String> shas = fetchAllCommitShas(owner, repo, branch, githubToken);

        // 3. Pour chaque sha, récupérer ses stats
        List<CommitDetailDto> details = shas.stream()
                .map(sha -> fetchCommitDetail(owner, repo, sha, githubToken))
                .collect(Collectors.toList());

        int totalCommits = details.size();
        int additions = details.stream().mapToInt(d -> d.getStats().getAdditions()).sum();
        int deletions = details.stream().mapToInt(d -> d.getStats().getDeletions()).sum();

        // 4. Calcul du score de cohérence de messages
        double consistency = calculateConsistency(details);

        // 5. Construction de l’entité
        CodeAnalysisResult result = CodeAnalysisResult.builder()
                .nomBrancheGit(branch)
                .brancheMergee(merged)
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

//    private boolean isBranchMerged(
//            String owner, String repo, String branch, String token
//    ) {
//        return webClient.get()
//                .uri(baseUrl + "/repos/{owner}/{repo}/pulls?head={owner}:{branch}&state=all",
//                        owner, repo, owner, branch)
//                .headers(h -> h.setBearerAuth(token))
//                .retrieve()
//                .bodyToFlux(PullRequestDto.class)
//                .retryWhen(backoffSpec())
//                .blockFirst(Duration.ofSeconds(10))  // on prend la 1ère PR
//                .getMerged_at() != null;
//    }
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

    return pr != null && pr.getMerged_at() != null;
}


    private List<String> fetchAllCommitShas(
            String owner, String repo, String branch, String token
    ) {
        List<String> shas = new ArrayList<>();
        String url = baseUrl + "/repos/{owner}/{repo}/commits?sha={branch}&per_page=100";
        String next = UriComponentsBuilder.fromUriString(url)
                .build(owner, repo, branch)
                .toString();

        do {
            ClientResponse resp = webClient.get()
                    .uri(next)
                    .headers(h -> h.setBearerAuth(token))
                    .exchange()
                    .retryWhen(backoffSpec())
                    .block();

            List<CommitSummaryDto> page = resp.bodyToFlux(CommitSummaryDto.class)
                    .collectList().block();
            shas.addAll(page.stream().map(CommitSummaryDto::getSha).toList());

            next = resp.headers().asHttpHeaders()
                    .getFirst(HttpHeaders.LINK);
            if (next != null && next.contains("rel=\"next\"")) {
                next = extractNextLink(next);
            } else {
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
