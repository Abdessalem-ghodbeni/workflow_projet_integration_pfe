package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TacheRequest {
    private String titre;
    private String description;
    private Complexity complexite;
    private Priorite priorite;
    private Long epicId;
    private Long projetId;
    private LocalDate dateDebutEstimee;
    private LocalDate dateFinEstimee;

}
