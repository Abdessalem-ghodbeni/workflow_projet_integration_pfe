package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOsStudentAnalytics.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISprintRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IStudentAnalysisReportRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITacheRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentAnalyticsService {
    private final ITacheRepository tacheRepository;
    private final ObjectMapper objectMapper;
    private final ISprintRepository sprintRepository;
    private final IStudentAnalysisReportRepository reportRepository;
    private final IEtudiantRepository etudiantRepository;

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

//    public AdvancedTaskMetrics calculateAdvancedMetrics(Long etudiantId) {
//        List<Tache> tasks = tacheRepository.findByEtudiantId(etudiantId);
//
//        return AdvancedTaskMetrics.builder()
//                .lowCommitPenalty(calculateCommitPenalty(tasks))
//                .potentialPlagiarismRisk(hasPotentialPlagiarismPattern(tasks))
//                .blockRate(calculateBlockRate(tasks))
//                .frequentBlockingIssue(hasFrequentBlocking(tasks))
//                .stateTransitionTimes(calculateStateTransitionTimes(tasks))
//                .build();
//    }

    // 1. Pénalité pour faible nombre de commits
    private double calculateCommitPenalty(List<Tache> tasks) {
        long penalizedTasks = tasks.stream()
                .filter(t -> t.getAnalyses().stream()
                        .mapToInt(CodeAnalysisResult::getNombreCommits)
                        .sum() < 3)
                .count();

        return Math.min(1.0, penalizedTasks * 0.2); // 20% par tâche problématique
    }
    private List<LocalDateTime> parseCommitDates(CodeAnalysisResult analysis) {
        if (analysis.getJourTravailDistribution() == null ||
                analysis.getHeureTravailDistribution() == null) {
            log.warn("Données de distribution manquantes pour l'analyse {}", analysis.getId());
            return Collections.emptyList();
        }

        try {

            // 1. Parser les distributions jour/heure
            Map<DayOfWeek, Integer> days = objectMapper.readValue(
                    analysis.getJourTravailDistribution(),
                    new TypeReference<Map<DayOfWeek, Integer>>(){}
            );

            Map<Integer, Integer> hours = objectMapper.readValue(
                    analysis.getHeureTravailDistribution(),
                    new TypeReference<Map<Integer, Integer>>(){}
            );

            // 2. Générer des dates approximatives
            List<LocalDateTime> dates = new ArrayList<>();
            LocalDate startDate = analysis.getDateDerniereAnalyseGit()
                    .minusDays(analysis.getBranchLifespanDays())
                    .toLocalDate();

            days.forEach((day, dayCount) -> {
                hours.forEach((hour, hourCount) -> {
                    LocalDate date = startDate.with(TemporalAdjusters.nextOrSame(day));
                    dates.add(LocalDateTime.of(date, LocalTime.of(hour, 0)));
                });
            });

            return dates;

        } catch (Exception e) {
            log.error("Error parsing commit dates", e);
            return Collections.emptyList();
        }
    }
    // 2. Détection de commits groupés
    private boolean hasPotentialPlagiarismPattern(List<Tache> tasks) {
        return tasks.stream().anyMatch(t ->
                t.getAnalyses().stream().anyMatch(a -> {
                    List<LocalDateTime> commits = parseCommitDates(a);

                    // Vérifier si tous commits en <24h
                    if (commits.size() < 2) return false;

                    LocalDateTime first = Collections.min(commits);
                    LocalDateTime last = Collections.max(commits);
                    return Duration.between(first, last).toHours() < 24;
                })
        );
    }

    // 3. Taux de blocage
    private double calculateBlockRate(List<Tache> tasks) {
        long blockedTasks = tasks.stream()
                .filter(t -> t.getHistoriqueEtats().stream()
                        .anyMatch(h -> h.getNouveauEtat() == EtatTache.BLOCKED))
                .count();

        return (double) blockedTasks / tasks.size();
    }

    // 4. Blocages fréquents
    private boolean hasFrequentBlocking(List<Tache> tasks) {
        return tasks.stream().anyMatch(t ->
                t.getHistoriqueEtats().stream()
                        .filter(h -> h.getNouveauEtat() == EtatTache.BLOCKED)
                        .count() > 2
        );
    }

    // 5. Temps entre états
    private Map<String, Double> calculateStateTransitionTimes(List<Tache> tasks) {
        return tasks.stream()
                .flatMap(t -> calculateTaskTransitions(t).entrySet().stream())
                .collect(Collectors.groupingBy(
                        Entry::getKey,
                        Collectors.averagingDouble(Entry::getValue)
                ));
    }

    private Map<String, Double> calculateTaskTransitions(Tache tache) {
        Map<String, Double> transitions = new HashMap<>();
        List<EtatHistoriqueTache> sorted = tache.getHistoriqueEtats().stream()
                .sorted(Comparator.comparing(EtatHistoriqueTache::getDateChangement))
                .toList();

        for (int i = 1; i < sorted.size(); i++) {
            EtatTache from = sorted.get(i-1).getNouveauEtat();
            EtatTache to = sorted.get(i).getNouveauEtat();
            double hours = Duration.between(
                    sorted.get(i-1).getDateChangement(),
                    sorted.get(i).getDateChangement()
            ).toHours();

            String key = from + "_TO_" + to;
            transitions.put(key, hours);
        }

        return transitions;
    }
    private List<Tache> getTasks(Long etudiantId, Long sprintId) {
        return (sprintId != null)
                ? tacheRepository.findByEtudiantIdAndSprintId(etudiantId, sprintId)
                : tacheRepository.findByEtudiantId(etudiantId);
    }
    public Map<String, TransitionMetrics> calculateTransitionMetrics(List<Tache> tasks) {
        Map<String, List<Duration>> transitions = new HashMap<>();

        for (Tache t : tasks) {
            List<EtatHistoriqueTache> history = t.getHistoriqueEtats()
                    .stream()
                    .sorted(Comparator.comparing(EtatHistoriqueTache::getDateChangement))
                    .toList();

            for (int i = 1; i < history.size(); i++) {
                EtatTache from = history.get(i-1).getNouveauEtat();
                EtatTache to = history.get(i).getNouveauEtat();

                Duration d = Duration.between(
                        history.get(i-1).getDateChangement(),
                        history.get(i).getDateChangement()
                );

                String key = from + "→" + to;
                transitions.computeIfAbsent(key, k -> new ArrayList<>()).add(d);
            }
        }

        return transitions.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> TransitionMetrics.builder() // Utilisation du Builder Lombok
                                .avgHours(e.getValue().stream()
                                        .mapToLong(Duration::toHours)
                                        .average()
                                        .orElse(0))
                                .count(e.getValue().size())
                                .build()
                ));
    }
//    public AdvancedTaskMetrics calculateAdvancedMetrics(Long etudiantId) {
//        List<Tache> tasks = tacheRepository.findByEtudiantId(etudiantId);
private void validateSprint(Long sprintId) {
    if (sprintId != null && !sprintRepository.existsById(sprintId)) {
        throw new IllegalArgumentException("Sprint non trouvé");
    }
}

    public AdvancedTaskMetrics calculateAdvancedMetrics(Long etudiantId, Long sprintId) {
        validateSprint(sprintId);
    List<Tache> tasks = getTasks(etudiantId, sprintId);
        Map<String, TransitionMetrics> transitions = calculateTransitions(tasks);

        return AdvancedTaskMetrics.builder()
//                .lowCommitPenalty(calculateLowCommitPenalty(tasks))
//                .stateTransitions(transitions)
//                .stagnationPenalty(calculateStagnationPenalty(transitions))
//                .reactivityScore(calculateReactivityScore(transitions))
//                // Conversion pour backward compatibility
//                .stateTransitionTimes(convertToLegacyFormat(transitions))
                .lowCommitPenalty(calculateLowCommitPenalty(tasks))
                .potentialPlagiarismRisk(hasPotentialPlagiarismPattern(tasks))
                .blockRate(calculateBlockRate(tasks))
                .frequentBlockingIssue(hasFrequentBlocking(tasks))
                .stateTransitions(calculateTransitions(tasks))
                .stagnationPenalty(calculateStagnationPenalty(calculateTransitions(tasks)))
                .reactivityScore(calculateReactivityScore(calculateTransitions(tasks)))
                .build();
    }
    private double calculateLowCommitPenalty(List<Tache> tasks) {
        return tasks.stream()
                .filter(t -> t.getAnalyses().stream()
                        .mapToInt(CodeAnalysisResult::getNombreCommits)
                        .sum() < 3
                )
                .count() * 0.2;
    }
    private double calculateStagnationPenalty(Map<String, TransitionMetrics> transitions) {
        return Optional.ofNullable(transitions.get("TO_DO_TO_IN_PROGRESS"))
                .map(tm -> Math.min(1.0, tm.getAvgHours() / 72))
                .orElse(0.0);
    }

    private double calculateReactivityScore(Map<String, TransitionMetrics> transitions) {
        return Optional.ofNullable(transitions.get("IN_REVIEW_TO_IN_TEST"))
                .map(tm -> 1 - Math.min(1.0, tm.getAvgHours() / 48))
                .orElse(1.0); // Meilleur score si aucune donnée
    }

    // Méthode utilitaire de conversion
    private Map<String, Double> convertToLegacyFormat(Map<String, TransitionMetrics> transitions) {
        return transitions.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        e -> e.getValue().getAvgHours()
                ));
    }




    private Map<String, TransitionMetrics> calculateTransitions(List<Tache> tasks) {
        return tasks.stream()
                .flatMap(t -> calculateTransitionMetrics(t).entrySet().stream())
                .collect(Collectors.toMap(
                        Entry::getKey,
                        Entry::getValue,
                        (v1, v2) -> new TransitionMetrics(
                                (v1.getAvgHours() + v2.getAvgHours()) / 2,
                                v1.getCount() + v2.getCount()
                        )
                ));
    }

    private Map<String, TransitionMetrics> calculateTransitionMetrics(Tache tache) {
        Map<String, TransitionMetrics> metrics = new HashMap<>();
        List<EtatHistoriqueTache> history = tache.getHistoriqueEtats()
                .stream()
                .sorted(Comparator.comparing(EtatHistoriqueTache::getDateChangement))
                .toList();

        for (int i = 1; i < history.size(); i++) {
            EtatTache from = history.get(i-1).getNouveauEtat();
            EtatTache to = history.get(i).getNouveauEtat();
            long hours = Duration.between(
                    history.get(i-1).getDateChangement(),
                    history.get(i).getDateChangement()
            ).toHours();

            String key = from + "_TO_" + to;
            metrics.merge(key,
                    new TransitionMetrics(hours, 1),
                    (v1, v2) -> new TransitionMetrics(
                            (v1.getAvgHours() * v1.getCount() + v2.getAvgHours() * v2.getCount()) / (v1.getCount() + v2.getCount()),
                            v1.getCount() + v2.getCount()
                    )
            );
        }

        return metrics;
    }



    //////// exposer api qui englobe tous si slouma
    public StudentAnalyticsReport generateFullAnalyticsReport(Long etudiantId,Long sprintId) {
        return StudentAnalyticsReport.builder()
                .workRegularity(calculateWorkRegularity(etudiantId))
                .commitImpact(analyzeCommitImpact(etudiantId))
                .taskEngagement(analyzeTaskEngagement(etudiantId))
                .advancedMetrics(calculateAdvancedMetrics(etudiantId,sprintId))
                .build();
    }

    public StudentAnalyticsReport generateAndSaveFullReport(Long etudiantId, Long sprintId) throws Exception {
        // 1. Générer le rapport DTO
        StudentAnalyticsReport reportDTO = generateFullAnalyticsReport(etudiantId, sprintId);

        // 2. Récupérer les entités associées
        Etudiant etudiant = etudiantRepository.getReferenceById(etudiantId); // ✅ Méthode JPA
        Sprint sprint = sprintId != null ? sprintRepository.getReferenceById(sprintId) : null;

        // 3. Convertir en JSON
        String jsonReport = objectMapper.writeValueAsString(reportDTO);

        // 4. Construire l'entité de rapport
        StudentAnalysisReport entity = StudentAnalysisReport.builder()
                .etudiant(etudiant)
                .sprint(sprint)
                .fullReportJson(jsonReport)
                .build();

        // 5. Sauvegarder
        reportRepository.save(entity);

        return reportDTO;
    }
///////////////////////////////////////////dfdsfdsfsdfsdfdsfsdfsdfsdfsdfdfsdf
public EnhancedEvaluationResult evaluateTask(Long taskId) {
    Tache tache = tacheRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));

    List<CodeAnalysisResult> analyses = tache.getAnalyses();
    if (analyses == null || analyses.isEmpty()) {
        throw new RuntimeException("Aucune analyse disponible");
    }

    // Calcul des métriques
    CodeAnalysisResult latestAnalysis = analyses.stream()
            .max(Comparator.comparing(CodeAnalysisResult::getDateDerniereAnalyseGit))
            .orElseThrow();

    // Calcul des différentes catégories
    EnhancedEvaluationResult.CodeHealthMetrics codeHealth = calculateCodeHealth(analyses);
    EnhancedEvaluationResult.DevelopmentBehavior behavior = calculateDevelopmentBehavior(latestAnalysis);
    EnhancedEvaluationResult.CollaborationMetrics collaboration = calculateCollaborationMetrics(analyses);
    EnhancedEvaluationResult.TemporalAnalysis temporal = calculateTemporalAnalysis(analyses);
    EnhancedEvaluationResult.CompositeScores scores = calculateCompositeScores(codeHealth, behavior, collaboration, temporal);
    List<String> recommendations = generateRecommendations(scores);

    return EnhancedEvaluationResult.builder()
            .codeHealth(codeHealth)
            .behavior(behavior)
            .collaboration(collaboration)
            .progression(temporal)
            .scores(scores)
            .recommendations(recommendations)
            .build();
}

    private EnhancedEvaluationResult.CodeHealthMetrics calculateCodeHealth(List<CodeAnalysisResult> analyses) {
        double stabilityIndex = calculateCodeStability(analyses);
        int criticalIssues = analyses.stream()
                .mapToInt(a -> (a.getCriticalBugs() != null ? a.getCriticalBugs() : 0) +
                        (a.getSecurityVulnerabilities() != null ? a.getSecurityVulnerabilities() : 0))
                .sum();

        return EnhancedEvaluationResult.CodeHealthMetrics.builder()
                .codeStabilityIndex(stabilityIndex)
                .criticalIssuesDensity(criticalIssues)
                .codeSustainability(analyses.stream()
                        .mapToDouble(a -> a.getLignesCodeAjoutees() - a.getLignesCodeSupprimees())
                        .average().orElse(0))
                .testCoverageTrend(0) // À implémenter avec données de couverture tests
                .build();
    }

    private Map<String, Integer> parseJson(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Integer>>() {});
        } catch (Exception e) {
            log.warn("Erreur de parsing JSON : {}", e.getMessage());
            return new HashMap<>();
        }
    }
    private EnhancedEvaluationResult.DevelopmentBehavior calculateDevelopmentBehavior(CodeAnalysisResult analysis) {
        Map<String, Integer> hours = parseJson(analysis.getHeureTravailDistribution());
        double focusFactor = calculateFocusFactor(analysis);

        return EnhancedEvaluationResult.DevelopmentBehavior.builder()
                .focusFactor(focusFactor)
                .workflowConsistency(calculateWorkflowConsistency(hours))
                .refactoringRatio(calculateRefactoringRatio(analysis))
                .crisisIndicator(calculateCrisisIndicator(analysis))
                .build();
    }

    private EnhancedEvaluationResult.CollaborationMetrics calculateCollaborationMetrics(List<CodeAnalysisResult> analyses) {
        return EnhancedEvaluationResult.CollaborationMetrics.builder()
                .mergeEfficiency(analyses.stream()
                        .mapToDouble(a -> a.getBranchLifespanDays() != null ?
                                a.getBranchLifespanDays() : 0)
                        .average().orElse(0))
                .conflictResolutionSkill(analyses.stream()
                        .filter(a -> a.isBrancheMergee())
                        .count() / (double) analyses.size())
                .reviewResponsiveness(0) // À implémenter avec données de revue
                .documentationQuality(0) // À implémenter avec analyse de documentation
                .build();
    }

    private EnhancedEvaluationResult.TemporalAnalysis calculateTemporalAnalysis(List<CodeAnalysisResult> analyses) {
        return EnhancedEvaluationResult.TemporalAnalysis.builder()
                .learningCurve(calculateLearningCurve(analyses))
                .consistencyScore(analyses.size() > 1 ? 1 : 0)
                .phaseTransitions(Collections.singletonList("Initial→Development"))
                .recoveryCapacity(0.8) // À affiner avec analyse des régressions
                .build();
    }

    private EnhancedEvaluationResult.CompositeScores calculateCompositeScores(
            EnhancedEvaluationResult.CodeHealthMetrics code,
            EnhancedEvaluationResult.DevelopmentBehavior behavior,
            EnhancedEvaluationResult.CollaborationMetrics collaboration,
            EnhancedEvaluationResult.TemporalAnalysis temporal) {

        return EnhancedEvaluationResult.CompositeScores.builder()
                .technicalMastery(0.3*code.getCodeStabilityIndex() +
                        0.4*behavior.getRefactoringRatio() +
                        0.3*((double) 1 /(code.getCriticalIssuesDensity()+1)))
                .teamContribution(0.5*collaboration.getMergeEfficiency() +
                        0.5*behavior.getFocusFactor())
                .projectOwnership(0.6*temporal.getConsistencyScore() +
                        0.4*code.getCodeSustainability())
                .agileMaturity(0.4*collaboration.getConflictResolutionSkill() +
                        0.3*temporal.getLearningCurve() +
                        0.3*behavior.getWorkflowConsistency())
                .build();
    }

    private List<String> generateRecommendations(EnhancedEvaluationResult.CompositeScores scores) {
        List<String> recs = new ArrayList<>();

//        if (scores.getTechnicalMastery() < 0.6) {
//            recs.add("Améliorer la qualité technique du code");
//        }
//        if (scores.getTeamContribution() < 0.7) {
//            recs.add("Renforcer la collaboration avec l'équipe");
//        }
//        if (scores.getProjectOwnership() < 0.5) {
//            recs.add("Développer un engagement plus fort vis-à-vis du projet");
//        }
        if (scores.getTechnicalMastery() < 5.0) {  // Ancien seuil : 0.6
            recs.add("Améliorer la qualité technique du code (refactoring, tests, documentation)");
        }
        if (scores.getTeamContribution() < 10.0) { // Ancien seuil : 0.7
            recs.add("Renforcer la collaboration avec l'équipe (revues de code, communication)");
        }
        if (scores.getProjectOwnership() < 2000.0) { // Ancien seuil : 0.5
            recs.add("Développer un engagement plus fort vis-à-vis du projet");
        }

        // Recommandation par défaut si aucune autre condition n'est remplie
        if (recs.isEmpty()) {
            recs.add("Bon travail général ! Aucun point critique identifié.");
        }

        return recs;
    }

    // Vos méthodes existantes à intégrer ici
    double calculateCodeStability(List<CodeAnalysisResult> analyses) {
        return analyses.stream()
                .mapToDouble(a -> (a.getLignesCodeAjoutees() + 1.0) /
                        (a.getLignesCodeSupprimees() + 1.0))
                .average().orElse(0.0);
    }

    double calculateCrisisIndicator(CodeAnalysisResult analysis) {
        Map<String, Integer> hours = parseJson(analysis.getHeureTravailDistribution());
        return hours.entrySet().stream()
                .filter(e -> Integer.parseInt(e.getKey()) < 6 ||
                        Integer.parseInt(e.getKey()) > 22)
                .mapToInt(Map.Entry::getValue)
                .sum() / (double) analysis.getNombreCommits();
    }

    double calculateRefactoringRatio(CodeAnalysisResult analysis) {
        Map<String, Integer> commitTypes = parseJson(analysis.getCommitTypesDistribution());
        return commitTypes.getOrDefault("refactor", 0) /
                (double) analysis.getNombreCommits();
    }

    double calculateLearningCurve(List<CodeAnalysisResult> analyses) {
        List<Double> qualityScores = analyses.stream()
                .sorted(Comparator.comparing(CodeAnalysisResult::getDateDerniereAnalyseGit))
                .map(this::calculateQualityScore)
                .collect(Collectors.toList());

        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < qualityScores.size(); i++) {
            regression.addData(i, qualityScores.get(i));
        }

        return regression.getSlope();
    }
    private double calculateQualityScore(CodeAnalysisResult analysis) {
        // Poids des différentes métriques (ajustables)
        double stabilityWeight = 0.4;
        double commitQualityWeight = 0.3;
        double commitFrequencyWeight = 0.2;
        double conflictFreeWeight = 0.1;

        // Calcul de la stabilité du code (ajouts / suppressions)
        double stability = (analysis.getLignesCodeAjoutees() + 1.0) /
                (analysis.getLignesCodeSupprimees() + 1.0);

        // Qualité des messages de commit (si disponible)
        double commitQuality = Optional.ofNullable(analysis.getScoreQualiteCommitMessage())
                .orElse(0.5); // Valeur par défaut si null

        // Fréquence des commits (inverse du nombre de commits)
        double commitFrequency = 1.0 / (analysis.getNombreCommits() + 1);

        // Absence de conflits (1 si aucun conflit, 0 sinon)
        double conflictFree = analysis.isBrancheMergee() && !analysis.getMergeConflictDetected() ? 1.0 : 0.0;

        // Score final basé sur les poids
        return stability * stabilityWeight +
                commitQuality * commitQualityWeight +
                commitFrequency * commitFrequencyWeight +
                conflictFree * conflictFreeWeight;
    }
    double calculateWorkflowConsistency(Map<String, Integer> hours) {
        double moyenne = hours.values().stream().mapToInt(Integer::intValue).average().orElse(0);
        return Math.sqrt(hours.values().stream()
                .mapToDouble(v -> Math.pow(v - moyenne, 2))
                .average().orElse(0));
    }

    double calculateFocusFactor(CodeAnalysisResult analysis) {
        Map<String, Integer> days = parseJson(analysis.getJourTravailDistribution());
        return days.size() / 7.0;
    }

}
