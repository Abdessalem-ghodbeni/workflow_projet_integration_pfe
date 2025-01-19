package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.Data;

@Data
public class ProjetRequest {
    private String nom;
    private Long equipeId;
    private String sujetProjet;
}
