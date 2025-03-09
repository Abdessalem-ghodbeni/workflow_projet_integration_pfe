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
                                    candidature.getMotivation()
                            ))
                            .collect(Collectors.toList());
                    return new SubjectCandidatureDTO(cleanedDescription, uniqueTeams);
                })
                .collect(Collectors.toList());

    }


    public Map<String, Object> getCandidaturesWithScores() {
        // Étape 1 : Récupérer les candidatures groupées
        List<SubjectCandidatureDTO> candidaturesGrouped = getCandidaturesGroupedBySubject();

        // Étape 2 : Envoyer ces candidatures à l’API Flask et récupérer les résultats
        return flaskIAService.sendDataToFlask(candidaturesGrouped);
    }

}
