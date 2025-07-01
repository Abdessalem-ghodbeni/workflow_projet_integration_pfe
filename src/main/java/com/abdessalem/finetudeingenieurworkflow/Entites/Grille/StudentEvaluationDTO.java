package com.abdessalem.finetudeingenieurworkflow.Entites.Grille;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEvaluationDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long gridId;
    private String gridTitle;
    private int academicYear;
    private String option;
    private double totalScore;
    private LocalDate evaluationDate;
}
