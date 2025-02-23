package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FilterDTO {
    private List<String> thematiques;
    private List<Integer> annees;
    private List<String> societes;
  
    private List<String> specialites;
    private List<Etat> etats;
}
