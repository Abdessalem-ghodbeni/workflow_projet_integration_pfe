package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.Data;

@Data
public class TacheRequest {
    private String titre;
    private String description;
    private Complexity complexite;
    private Priorite priorite;
    private Long epicId;
    private Long projetId;
}
