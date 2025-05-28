package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.ProductivityComparisonRequest;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.StatisticsDTO;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.TutorProductivityDTO;
import com.abdessalem.finetudeingenieurworkflow.Entites.Etat;
import com.abdessalem.finetudeingenieurworkflow.Entites.EtatEquipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.Societe;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import com.abdessalem.finetudeingenieurworkflow.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final ITuteurRepository tuteurRepository;
    private final ISujetRepository sujetRepository;
    private final ISocieteRepository societeRepository;
    private final IEtudiantRepository etudiantRepository;
    private final IEquipeRepository equipeRepository;
    private final IProjetRepository projetRepository;

    public StatisticsDTO getPlatformStatistics() {
        Map<String, Long> subjectsByStatus = sujetRepository.countSubjectsByStatusGrouped()
                .stream()
                .collect(Collectors.toMap(
                        array -> ((Etat) array[0]).name(),
                        array -> (Long) array[1]
                ));

        // Faites de même pour les équipes
        Map<String, Long> teamsByStatus = equipeRepository.countTeamsByStatusGrouped()
                .stream()
                .collect(Collectors.toMap(
                        array -> ((EtatEquipe) array[0]).name(),
                        array -> (Long) array[1]
                ));
        return new StatisticsDTO(
                tuteurRepository.count(),
                sujetRepository.count(),
                sujetRepository.countByEtat(Etat.ACCEPTED),
                sujetRepository.countByEtat(Etat.REFUSER),
                sujetRepository.countByEtat(Etat.ENCOURS),
                societeRepository.count(),
                etudiantRepository.count(),
                tuteurRepository.countChefOptions(),
                tuteurRepository.countChefDepartement(),
                equipeRepository.count(),
                equipeRepository.countByEtat(EtatEquipe.AUTORISE),
                subjectsByStatus,
                teamsByStatus
        );
    }

    public List<TutorProductivityDTO> compareTutorProductivity(ProductivityComparisonRequest request) {
        List<Tuteur> tutors = request.tutorIds().isEmpty() ?
                tuteurRepository.findAll() :
                tuteurRepository.findAllById(request.tutorIds());

        int currentYear = Year.now().getValue();
        int yearFilter = request.year() != null ? request.year() : currentYear;

        return tutors.stream().map(tutor -> {
            // Gérer les valeurs nulles
            Double avgTeamSize = equipeRepository.averageTeamSize(tutor);
            if(avgTeamSize == null) avgTeamSize = 0.0;

            return new TutorProductivityDTO(
                    tutor.getId(),
                    tutor.getNom() + " " + tutor.getPrenom(),
                    sujetRepository.countByTuteurAndYear(tutor, yearFilter),
                    equipeRepository.countByTuteurAndYear(tutor, yearFilter),
                    avgTeamSize, // Utilisez Double au lieu de double
                    projetRepository.countCompletedByTuteur(tutor, yearFilter)
            );
        }).toList();
    }
    // Statistiques supplémentaires
    public Map<String, Long> getSubjectDistributionByCompany() {
        return societeRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Societe::getNom,
                        s -> (long) s.getSujets().size()
                ));
    }

    public Map<String, Long> getStudentDistributionBySpecialty() {
        List<Object[]> results = etudiantRepository.countStudentsBySpecialty();

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> ((Number) result[1]).longValue()
                ));
    }

    public Map<Integer, Long> getProjectTimeline(Long tutorId) {
        List<Object[]> results = projetRepository.countProjectsByMonth(tutorId);

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0],
                        result -> ((Number) result[1]).longValue()
                ));
    }
}
