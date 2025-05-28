package com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique;

public record TutorProductivityDTO(
        Long tutorId,
        String tutorName,
        int subjectsProposed,
        int teamsSupervised,
        double averageTeamSize,
        int projectsCompleted
) {
}
