package com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SujetEvolutionDTO {
    private Integer annee;
    private Long nombreSujets;
}
