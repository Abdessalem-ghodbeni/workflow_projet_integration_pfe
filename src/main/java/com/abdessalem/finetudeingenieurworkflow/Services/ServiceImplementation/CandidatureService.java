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

                    // Récupérer la description associée au sujet
                    Optional<Sujet> sujet = sujetRepository.findByTitre(subjectTitle);
                    String rawDescription = sujet.map(Sujet::getDescription).orElse("Description indisponible");

                    // Nettoyer la description HTML
                    String cleanedDescription = HtmlCleanerUtil.cleanHtml(rawDescription);
                    Set<Long> seenTeams = new HashSet<>(); // Set pour éviter les doublons

                    List<TeamMotivationDTO> uniqueTeams = entry.getValue().stream()
                            .filter(candidature -> seenTeams.add(candidature.getEquipe().getId())) // Ajoute l'équipe si elle n'est pas encore présente
                            .map(candidature -> new TeamMotivationDTO(
                                    candidature.getEquipe().getId(),
                                    candidature.getMotivation(), 0.0

                            ))
                            .collect(Collectors.toList());
                    return new SubjectCandidatureDTO(subjectTitle,cleanedDescription, uniqueTeams);
                })
                .collect(Collectors.toList());

    }


    public List<SubjectCandidatureDTO> getCandidaturesWithScores() {
        List<SubjectCandidatureDTO> candidaturesGrouped = getCandidaturesGroupedBySubject();

        // Appel à l'API Flask pour obtenir les scores
        Map<String, Object> response = flaskIAService.sendDataToFlask(candidaturesGrouped);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        if (results == null) return candidaturesGrouped; // Si Flask ne répond pas, on retourne les données sans score

        // Associer les scores en gardant `subjectTitle`
        return candidaturesGrouped.stream()
                .map(subject -> {
                    // Trouver le résultat correspondant venant de Flask (par la description)
                    Map<String, Object> flaskResult = results.stream()
                            .filter(res -> res.get("subjectDescription").toString().equals(subject.getSubjectDescription()))
                            .findFirst()
                            .orElse(null);

                    if (flaskResult == null) return subject; // Si pas de correspondance, on garde les données brutes

                    // Récupération des équipes avec les scores
                    List<Map<String, Object>> teams = (List<Map<String, Object>>) flaskResult.get("teams");

                    List<TeamMotivationDTO> sortedTeams = teams.stream()
                            .map(team -> new TeamMotivationDTO(
                                    Long.valueOf(team.get("teamId").toString()),
                                    team.get("motivation").toString(),
                                    Double.valueOf(team.get("score").toString())  // Ajout du score
                            ))
                            .sorted(Comparator.comparingDouble(TeamMotivationDTO::getScore).reversed()) // Tri décroissant
                            .collect(Collectors.toList());

                    // Retourne le sujet avec son titre, sa description et les équipes triées
                    return new SubjectCandidatureDTO(subject.getSubjectTitle(), subject.getSubjectDescription(), sortedTeams);
                })
                .collect(Collectors.toList());
    }


}
