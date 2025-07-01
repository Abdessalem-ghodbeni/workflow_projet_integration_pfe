package com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormStatsDTO {
    private Long formId;
    private String formTitle;
    private Long responseCount;
}
