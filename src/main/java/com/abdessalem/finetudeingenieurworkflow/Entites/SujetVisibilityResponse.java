package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class SujetVisibilityResponse {
    private List<Long> sujetIds;
    private boolean visibleAuxEtudiants;
    private String message;
}
