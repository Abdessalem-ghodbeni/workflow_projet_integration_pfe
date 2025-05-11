package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.CodeAnalysisResult;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics.CommitImpactProfile;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics.TaskEngagement;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics.WorkRegularityScore;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITacheRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentAnalyticsService {
    private final ITacheRepository tacheRepository;
    private final ObjectMapper objectMapper;

    // Métrique clé 1: Régularité du travail
    public WorkRegularityScore calculateWorkRegularity(Long etudiantId) {
        List<CodeAnalysisResult> analyses = getStudentAnalyses(etudiantId);

        Map<DayOfWeek, Integer> weeklyPattern = aggregateWeeklyPattern(analyses);
        Map<Integer, Integer> hourlyPattern = aggregateHourlyPattern(analyses);

        return WorkRegularityScore.builder()
                .consistencyScore(calculateConsistency(analyses))
                .peakDays(getTopDays(weeklyPattern, 2))
                .peakHours(getTopHours(hourlyPattern, 3))
                .weekendWorkRatio(calculateWeekendWorkRatio(weeklyPattern))
                .build();
    }
    private Map<Integer, Integer> aggregateHourlyPattern(List<CodeAnalysisResult> analyses) {
        return analyses.stream()
                .flatMap(a -> parseHourDistribution(a.getHeureTravailDistribution()).entrySet().stream())
                .collect(Collectors.toMap(
                        Entry::getKey,
                        Entry::getValue,
                        Integer::sum
                ));
    }

    private Map<Integer, Integer> parseHourDistribution(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public CommitImpactProfile analyzeCommitImpact(Long etudiantId) {
        List<CodeAnalysisResult> analyses = getAnalyses(etudiantId);

        return CommitImpactProfile.builder()
                .avgCommitSize(analyses.stream()
                        .mapToDouble(a ->
                                Optional.ofNullable(a.getAverageAdditionsPerCommit()).orElse(0.0) +
                                        Optional.ofNullable(a.getAverageDeletionsPerCommit()).orElse(0.0)
                        )
                        .average().orElse(0))
                .mergeConflictRate(analyses.stream()
                        .filter(a -> Boolean.TRUE.equals(a.getMergeConflictDetected()))
                        .count() / (double) analyses.size())
                .branchLongevity(analyses.stream()
                        .mapToLong(a -> Optional.ofNullable(a.getBranchLifespanDays()).orElse(0L))
                        .average().orElse(0))
                .build();
    }
    private List<String> getTopDays(Map<DayOfWeek, Integer> weeklyDistribution, int limit) {
        return weeklyDistribution.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(entry -> translateDay(entry.getKey()))
                .collect(Collectors.toList());
    }

    // Helper pour traduction FR
    private String translateDay(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "LUNDI";
            case TUESDAY -> "MARDI";
            case WEDNESDAY -> "MERCREDI";
            case THURSDAY -> "JEUDI";
            case FRIDAY -> "VENDREDI";
            case SATURDAY -> "SAMEDI";
            case SUNDAY -> "DIMANCHE";
        };
    }
    // Métrique clé 3: Analyse des justifications/suggestions
    public TaskEngagement analyzeTaskEngagement(Long etudiantId) {
        List<Tache> tasks = tacheRepository.findByEtudiantId(etudiantId);

        return TaskEngagement.builder()
                .justificationRatio((double) tasks.stream()
                        .mapToInt(t -> t.getJustifications().size())
                        .sum() / tasks.size())
                .suggestionAdoptionRate(calculateSuggestionAdoption(tasks))
                .delayedTaskRate((double) tasks.stream()
                        .filter(Tache::isNotified)
                        .count() / tasks.size())
                .build();
    }

    // Helper: Récupération consolidée des analyses
    private List<CodeAnalysisResult> getStudentAnalyses(Long etudiantId) {
        return tacheRepository.findByEtudiantId(etudiantId).stream()
                .flatMap(t -> t.getAnalyses().stream())
                .toList();
    }
    private List<CodeAnalysisResult> getAnalyses(Long etudiantId) {
        return tacheRepository.findByEtudiantId(etudiantId).stream()
                .filter(t -> t.getAnalyses() != null)
                .flatMap(t -> t.getAnalyses().stream())
                .filter(a -> a.getDateDerniereAnalyseGit() != null)
                .collect(Collectors.toList());
    }
    // Helper: Calcul de l'adoption des suggestions
    private double calculateSuggestionAdoption(List<Tache> tasks) {
        long totalSuggestions = tasks.stream()
                .mapToInt(t -> t.getSuggestions().size())
                .sum();

        long implementedSuggestions = tasks.stream()
                .flatMap(t -> t.getSuggestions().stream())
                .filter(s -> s.getDateModification().isAfter(s.getDateCreation()))
                .count();

        return totalSuggestions > 0 ? (double) implementedSuggestions / totalSuggestions : 0;
    }

    // Méthodes de parsing des distributions
    private Map<DayOfWeek, Integer> aggregateWeeklyPattern(List<CodeAnalysisResult> analyses) {
        return analyses.stream()
                .flatMap(a -> parseDistribution(a.getJourTravailDistribution(), DayOfWeek.class).entrySet().stream())
                .collect(Collectors.toMap(
                        Entry::getKey,
                        Entry::getValue,
                        Integer::sum
                ));
    }

    private <T extends Enum<T>> Map<T, Integer> parseDistribution(String json, Class<T> enumType) {
        try {
            Map<String, Integer> rawMap = objectMapper.readValue(json, new TypeReference<>() {});
            return rawMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> Enum.valueOf(enumType, e.getKey()),
                            Entry::getValue
                    ));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private List<String> getTopHours(Map<Integer, Integer> hourlyDistribution, int limit) {
        return hourlyDistribution.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(entry -> String.format("%02dh", entry.getKey()))
                .collect(Collectors.toList());
    }
    private double calculateWeekendWorkRatio(Map<DayOfWeek, Integer> weeklyDistribution) {
        int weekendCommits = weeklyDistribution.getOrDefault(DayOfWeek.SATURDAY, 0)
                + weeklyDistribution.getOrDefault(DayOfWeek.SUNDAY, 0);

        int totalCommits = weeklyDistribution.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        return totalCommits > 0 ? (double) weekendCommits / totalCommits : 0.0;
    }
    private double calculateConsistency(List<CodeAnalysisResult> analyses) {
        List<Double> dailyDeviations = analyses.stream()
                .map(a -> {
                    Map<Integer, Integer> hours = parseHourDistribution(a.getHeureTravailDistribution());
                    return calculateDailyDeviation(hours);
                })
                .toList();

        return dailyDeviations.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private double calculateDailyDeviation(Map<Integer, Integer> hourlyDistribution) {
        if (hourlyDistribution.isEmpty()) return 0.0;

        double mean = hourlyDistribution.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        double variance = hourlyDistribution.values().stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0.0);

        return 1 - (Math.sqrt(variance) / mean); // 1 - coefficient de variation
    }
}
