package com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YearStats {
    private int sujetsValides;
    private int sujetsRefuses;
    private int etudiantsEncadres;
    private int equipesLiees;
}
