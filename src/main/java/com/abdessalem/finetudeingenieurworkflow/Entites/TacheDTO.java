package com.abdessalem.finetudeingenieurworkflow.Entites;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TacheDTO {
    private Long id;
    private String titre;
    private LocalDate dateDebutEstimee;
    private LocalDate dateFinEstimee;
    private String etat;
    private Priorite priorite;
    private Epic epic;
}
