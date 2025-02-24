package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class SujetAcceptedFiltersDTO {
    private List<String> thematiques;
    private List<String> specialites;
    private List<Integer> annees;
    private List<String> titres;
}
