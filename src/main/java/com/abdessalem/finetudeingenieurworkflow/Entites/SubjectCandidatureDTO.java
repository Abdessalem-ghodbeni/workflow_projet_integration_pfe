package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SubjectCandidatureDTO {

    private String subjectTitle;
    private String subjectDescription;
    private List<TeamMotivationDTO> teams;
}
