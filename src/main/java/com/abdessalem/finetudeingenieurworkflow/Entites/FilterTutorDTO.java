package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterTutorDTO {

    private List<String> thematiques;
    private List<Integer> annees;
    private List<String> titres;
    private List<String> tuteurs;
    private List<String> specialites;
    private List<Etat> etats;
}
