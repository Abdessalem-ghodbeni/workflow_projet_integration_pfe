package com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TuteurFormStatsDTO {
    private Long tuteurId;
    private Long totalForms;
    private Long totalResponses;
    private List<FormStatsDTO> formsStats;
}
