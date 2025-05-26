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
    private int sujetsTuteur;      //  Nombre de sujets du tuteur (validees + refusees)
    private int sujetsPlateforme;
    private int equipesLieesTuteur;     // 👈 Équipes du tuteur (année)
    private int equipesPlateforme;      // 👈 Total équipes plateforme (année)
    private int etudiantsEncadresTuteur; // 👈 Étudiants encadrés (année)
    private int etudiantsPlateforme;
}
