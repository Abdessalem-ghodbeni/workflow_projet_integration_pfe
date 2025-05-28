package com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique;

import java.util.Map;

public record StatisticsDTO(long totalTutors,
                            long totalSubjects,
                            long validatedSubjects,
                            long rejectedSubjects,
                            long pendingSubjects,
                            long totalCompanies,
                            long totalStudents,
                            long optionLeaders,
                            long departmentHeads,
                            long totalTeams,
                            long activeTeams,
                            Map<String, Long> subjectsByStatus,
                            Map<String, Long> teamsByStatus) {

}
