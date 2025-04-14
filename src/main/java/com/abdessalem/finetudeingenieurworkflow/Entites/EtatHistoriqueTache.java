package com.abdessalem.finetudeingenieurworkflow.Entites;

import java.time.LocalDateTime;

public class EtatHistoriqueTache {
    private EtatTache ancienEtat;
    private EtatTache nouveauEtat;
    private LocalDateTime dateChangement;
    private Etudiant acteur;
}
