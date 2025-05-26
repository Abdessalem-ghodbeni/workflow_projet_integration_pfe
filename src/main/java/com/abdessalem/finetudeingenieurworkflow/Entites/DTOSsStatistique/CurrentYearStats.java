package com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrentYearStats {
    private int sujetsValides;
    private int sujetsRefuses;
    private int etudiantsEncadres;
    private int equipesLiees;
}
