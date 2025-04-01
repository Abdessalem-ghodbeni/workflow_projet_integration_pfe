package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.ICandidatureRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISujetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
public class CandidatureService {
    private final ICandidatureRepository candidatureRepository;
    private final ISujetRepository sujetRepository;
    private final FlaskClientService flaskIAService;
    public List<SubjectCandidatureDTO> getCandidaturesGroupedBySubject() {
        List<Candidature> candidatures = candidatureRepository.findAll();

        // Regrouper les candidatures par titre de sujet
        Map<String, List<Candidature>> groupedBySubject = candidatures.stream()
                .collect(Collectors.groupingBy(Candidature::getSubjectTitle));

        // Construire la liste des DTO
        return groupedBySubject.entrySet().stream()
                .map(entry -> {
                    String subjectTitle = entry.getKey();

                    // raja3li la description associée au sujet
                    Optional<Sujet> sujet = sujetRepository.findByTitre(subjectTitle);
                    String rawDescription = sujet.map(Sujet::getDescription).orElse("Description indisponible");

                    // na4fu code html 5ater fih des imagesbase 64 w des lien ... HTML ya3ni 5aterni bch nitraba nbatel w n3oud n5dem simple
                    String cleanedDescription = HtmlCleanerUtil.cleanHtml(rawDescription);
                    Set<Long> seenTeams = new HashSet<>(); // Set pour éviter les doublons

                    List<TeamMotivationDTO> uniqueTeams = entry.getValue().stream()
                            .filter(candidature -> seenTeams.add(candidature.getEquipe().getId())) // Ajoute l'équipe si elle n'est pas encore présente
                            .map(candidature -> new TeamMotivationDTO(
                                    candidature.getEquipe().getId(), candidature.getEquipe().getNom(),
                                    candidature.getEquipe().getImage(),
                                    candidature.getMotivation(), 0.0

                            ))
                            .collect(Collectors.toList());
                    return new SubjectCandidatureDTO(subjectTitle,cleanedDescription, uniqueTeams);
                })
                .collect(Collectors.toList());

    }


    public List<SubjectCandidatureDTO> getCandidaturesWithScores() {
        List<SubjectCandidatureDTO> candidaturesGrouped = getCandidaturesGroupedBySubject();

        //   Flask bech raja3  les scores
        Map<String, Object> response = flaskIAService.sendDataToFlask(candidaturesGrouped);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        if (results == null) return candidaturesGrouped;


        return candidaturesGrouped.stream()
                .map(subject -> {

                    Map<String, Object> flaskResult = results.stream()
                            .filter(res -> res.get("subjectDescription").toString().equals(subject.getSubjectDescription()))
                            .findFirst()
                            .orElse(null);

                    if (flaskResult == null) return subject; // ken famech  correspondance n5aliw donnée brutes


                    List<Map<String, Object>> teams = (List<Map<String, Object>>) flaskResult.get("teams");

                    List<TeamMotivationDTO> sortedTeams = teams.stream()
                            .map(team -> {
                                Long teamId = Long.valueOf(team.get("teamId").toString());
                                String motivation = team.get("motivation").toString();
                                double score = Double.valueOf(team.get("score").toString());

                                // Rechercher l'équipe correspondante pour récupérer le nom et l'image
                                TeamMotivationDTO originalTeam = subject.getTeams().stream()
                                        .filter(t -> t.getTeamId().equals(teamId))
                                        .findFirst()
                                        .orElse(null);

                                if (originalTeam == null) {
                                    return new TeamMotivationDTO(teamId, "Nom inconnu", "Image inconnue", motivation, score);
                                }

                                return new TeamMotivationDTO(
                                        teamId,
                                        originalTeam.getTeamName(),
                                        originalTeam.getTeamImage(),
                                        motivation,
                                        score
                                );
                            })
                            .sorted(Comparator.comparingDouble(TeamMotivationDTO::getScore).reversed()) // Trier par score décroissant
                            .collect(Collectors.toList());

                    // custimi type de retour te3 api lil frontend
                    return new SubjectCandidatureDTO(subject.getSubjectTitle(), subject.getSubjectDescription(), sortedTeams);
                })
                .collect(Collectors.toList());
    }


}
