package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import com.abdessalem.finetudeingenieurworkflow.Entites.Grille.*;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import com.abdessalem.finetudeingenieurworkflow.Exception.InvalidScoreException;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.*;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IEvaluationServicesImp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService implements IEvaluationServicesImp {
    private final IStudentEvaluationRepository evaluationRepository;
    private final IEtudiantRepository studentRepository;
    private final ITuteurRepository tuteurRepository;
    private final IEvaluationGridRepository gridRepository;
    private final IEvaluationCriterionRepository criterionRepository;
    private final ICriterionLevelRepository levelRepository;
//@Override
//    @Transactional
//    public StudentEvaluation evaluateStudent(StudentEvaluationRequest request) {
//        // Validation des IDs
//        if (request.getStudentId() == null) throw new IllegalArgumentException("Student ID is required");
//        if (request.getTuteurId() == null) throw new IllegalArgumentException("Tutor ID is required");
//        if (request.getEvaluationGridId() == null) throw new IllegalArgumentException("Grid ID is required");
//
//        Etudiant student = studentRepository.findById(request.getStudentId())
//                .orElseThrow(() -> new RessourceNotFound("Étudiant non trouvé"));
//
//        Tuteur tuteur = tuteurRepository.findById(request.getTuteurId())
//                .orElseThrow(() -> new RessourceNotFound("Tuteur non trouvé"));
//
//        EvaluationGrid grid = gridRepository.findById(request.getEvaluationGridId())
//                .orElseThrow(() -> new RessourceNotFound("Grille d'évaluation non trouvée"));
//
//        // Création de l'évaluation principale avec liste initialisée
//        StudentEvaluation evaluation = StudentEvaluation.builder()
//                .student(student)
//                .tuteur(tuteur)
//                .evaluationGrid(grid)
//                .evaluationDate(LocalDate.now())
//                .criterionEvaluations(new ArrayList<>())
//                .build();
//
//        for (CriterionEvaluationRequest cer : request.getCriterionEvaluations()) {
//            if (cer.getCriterionId() == null) throw new IllegalArgumentException("Criterion ID is required");
//            if (cer.getSelectedLevelId() == null) throw new IllegalArgumentException("Level ID is required");
//
//            EvaluationCriterion criterion = criterionRepository.findById(cer.getCriterionId())
//                    .orElseThrow(() -> new RessourceNotFound("Critère non trouvé"));
//
//            CriterionLevel level = levelRepository.findById(cer.getSelectedLevelId())
//                    .orElseThrow(() -> new RessourceNotFound("Niveau de critère non trouvé"));
//
//            // Validation du score
//            if (cer.getAssignedScore() < level.getMinScore() || cer.getAssignedScore() > level.getMaxScore()) {
//                throw new InvalidScoreException(
//                        "Score invalide pour '" + criterion.getName() + "': " +
//                                cer.getAssignedScore() + " (intervalle: " +
//                                level.getMinScore() + "-" + level.getMaxScore() + ")"
//                );
//            }
//
//            CriterionEvaluation criterionEvaluation = CriterionEvaluation.builder()
//                    .criterion(criterion)
//                    .selectedLevel(level)
//                    .assignedScore(cer.getAssignedScore())
//                    .evaluation(evaluation)
//                    .build();
//
//            evaluation.getCriterionEvaluations().add(criterionEvaluation);
//        }
//
//        // Calcul du score total
//        double totalScore = evaluation.getCriterionEvaluations().stream()
//                .mapToDouble(CriterionEvaluation::getAssignedScore)
//                .sum();
//        evaluation.setTotalScore(totalScore);
//
//        return evaluationRepository.save(evaluation);
//    }
@Override
@Transactional
public ApiResponse evaluateStudent(StudentEvaluationRequest request) {
    try {
        // Validation des IDs
        if (request.getStudentId() == null) throw new IllegalArgumentException("ID étudiant requis");
        if (request.getTuteurId() == null) throw new IllegalArgumentException("ID tuteur requis");
        if (request.getEvaluationGridId() == null) throw new IllegalArgumentException("ID grille d'évaluation requis");

        Etudiant student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RessourceNotFound("Étudiant non trouvé"));

        Tuteur tuteur = tuteurRepository.findById(request.getTuteurId())
                .orElseThrow(() -> new RessourceNotFound("Tuteur non trouvé"));

        EvaluationGrid grid = gridRepository.findById(request.getEvaluationGridId())
                .orElseThrow(() -> new RessourceNotFound("Grille d'évaluation non trouvée"));

        // Création de l'évaluation
        StudentEvaluation evaluation = StudentEvaluation.builder()
                .student(student)
                .tuteur(tuteur)
                .evaluationGrid(grid)
                .evaluationDate(LocalDate.now())
                .criterionEvaluations(new ArrayList<>())
                .build();

        for (CriterionEvaluationRequest cer : request.getCriterionEvaluations()) {
            if (cer.getCriterionId() == null) throw new IllegalArgumentException("ID critère requis");
            if (cer.getSelectedLevelId() == null) throw new IllegalArgumentException("ID niveau requis");

            EvaluationCriterion criterion = criterionRepository.findById(cer.getCriterionId())
                    .orElseThrow(() -> new RessourceNotFound("Critère non trouvé"));

            CriterionLevel level = levelRepository.findById(cer.getSelectedLevelId())
                    .orElseThrow(() -> new RessourceNotFound("Niveau de critère non trouvé"));

            // Validation du score
            if (cer.getAssignedScore() < level.getMinScore() || cer.getAssignedScore() > level.getMaxScore()) {
                throw new InvalidScoreException(
                        "Score invalide pour '" + criterion.getName() + "': " +
                                cer.getAssignedScore() + " (intervalle: " +
                                level.getMinScore() + "-" + level.getMaxScore() + ")"
                );
            }

            CriterionEvaluation criterionEvaluation = CriterionEvaluation.builder()
                    .criterion(criterion)
                    .selectedLevel(level)
                    .assignedScore(cer.getAssignedScore())
                    .evaluation(evaluation)
                    .build();

            evaluation.getCriterionEvaluations().add(criterionEvaluation);
        }

        // Calcul du score total
        double totalScore = evaluation.getCriterionEvaluations().stream()
                .mapToDouble(CriterionEvaluation::getAssignedScore)
                .sum();
        evaluation.setTotalScore(totalScore);

        evaluationRepository.save(evaluation);

        return new ApiResponse("Évaluation enregistrée avec succès!", true);

    } catch (Exception e) {
        return new ApiResponse("Erreur: " + e.getMessage(), false);
    }
}
    @Override
    public List<CriterionLevel> suggestLevels(Long criterionId, double score) {
        if (criterionId == null) throw new IllegalArgumentException("Criterion ID is required");

        EvaluationCriterion criterion = criterionRepository.findById(criterionId)
                .orElseThrow(() -> new RessourceNotFound("Critère non trouvé"));

        return criterion.getLevels().stream()
                .filter(level -> score >= level.getMinScore() && score <= level.getMaxScore())
                .toList();
    }
    @Override
    @Transactional
    public EvaluationGrid createEvaluationGrid(EvaluationGridRequest request) {
        EvaluationGrid grid = EvaluationGrid.builder()
                .title(request.getTitle())
                .option(request.getOption())
                .academicYear(request.getAcademicYear())
                .criteria(new ArrayList<>())
                .build();

        for (CriterionRequest cr : request.getCriteria()) {
            EvaluationCriterion criterion = EvaluationCriterion.builder()
                    .name(cr.getName())
                    .description(cr.getDescription())
                    .maxScore(cr.getMaxScore())
                    .evaluationGrid(grid)
                    .levels(new ArrayList<>())
                    .build();

            for (LevelRequest lr : cr.getLevels()) {
                CriterionLevel level = CriterionLevel.builder()
                        .levelName(lr.getLevelName())
                        .description(lr.getDescription())
                        .minScore(lr.getMinScore())
                        .maxScore(lr.getMaxScore())
                        .criterion(criterion)
                        .build();
                criterion.getLevels().add(level);
            }
            grid.getCriteria().add(criterion);
        }

        return gridRepository.save(grid);
    }
    @Override
    public List<EvaluationGrid> getEvaluationGridsByYearAndOption(int academicYear, String option) {
        return gridRepository.findByAcademicYearAndOption(academicYear, option);
    }

    @Override
    public EvaluationGrid getEvaluationGridById(Long id) {
        return gridRepository.findById(id).orElseThrow(() -> new RessourceNotFound("Critère non trouvé"));
    }


    public List<StudentEvaluationDTO> getAllEvaluationsForCurrentYear() {
        List<StudentEvaluation> evaluations = evaluationRepository.findAllForCurrentYear();
        return transformToDTO(evaluations);
    }

    public List<StudentEvaluationDTO> getStudentEvaluationsForCurrentYear(Long studentId) {
        List<StudentEvaluation> evaluations = evaluationRepository.findByStudentIdForCurrentYear(studentId);
        return transformToDTO(evaluations);
    }

    private List<StudentEvaluationDTO> transformToDTO(List<StudentEvaluation> evaluations) {
        return evaluations.stream().map(eval -> {
            double totalScore = calculateTotalScore(eval);

            return StudentEvaluationDTO.builder()
                    .id(eval.getId())
                    .studentId(eval.getStudent().getId())
                    .studentName(eval.getStudent().getNom() + " " + eval.getStudent().getPrenom())
                    .gridId(eval.getEvaluationGrid().getId())
                    .gridTitle(eval.getEvaluationGrid().getTitle())
                    .academicYear(eval.getEvaluationGrid().getAcademicYear())
                    .option(eval.getEvaluationGrid().getOption())
                    .totalScore(totalScore)
                    .evaluationDate(eval.getEvaluationDate())
                    .build();
        }).collect(Collectors.toList());
    }

    private double calculateTotalScore(StudentEvaluation evaluation) {
        return evaluation.getCriterionEvaluations().stream()
                .mapToDouble(CriterionEvaluation::getAssignedScore)
                .sum();
    }

    public EvaluationDetailsDTO getEvaluationDetails(Long evaluationId) {
        StudentEvaluation evaluation = evaluationRepository.findByIdWithDetails(evaluationId)
                .orElseThrow(() -> new RessourceNotFound("Évaluation non trouvée"));
      String nomCompletTuteur=evaluation.getTuteur().getNom() + " " + evaluation.getTuteur().getPrenom();
        String nomCompletEtudiant=evaluation.getStudent().getNom() + " " + evaluation.getStudent().getPrenom();
        return EvaluationDetailsDTO.builder()
                .studentName(nomCompletEtudiant)
                .tuteurName(nomCompletTuteur)
                .gridTitle(evaluation.getEvaluationGrid().getTitle())
                .academicYear(evaluation.getEvaluationGrid().getAcademicYear())
                .option(evaluation.getEvaluationGrid().getOption())
                .gridVersion(evaluation.getEvaluationGrid().getVersion())
                .evaluationDate(evaluation.getEvaluationDate())
                .totalScore(evaluation.getTotalScore())
                .criteriaDetails(mapCriteriaDetails(evaluation))
                .build();
    }

    private List<CriterionEvaluationDetailDTO> mapCriteriaDetails(StudentEvaluation evaluation) {
        return evaluation.getCriterionEvaluations().stream()
                .map(ce -> CriterionEvaluationDetailDTO.builder()
                        .criterionName(ce.getCriterion().getName())
                        .description(ce.getCriterion().getDescription())
                        .maxScore(ce.getCriterion().getMaxScore())
                        .assignedScore(ce.getAssignedScore())
                        .selectedLevel(ce.getSelectedLevel() != null ?
                                ce.getSelectedLevel().getLevelName() : "Non spécifié")
                        .build())
                .collect(Collectors.toList());
    }







}


