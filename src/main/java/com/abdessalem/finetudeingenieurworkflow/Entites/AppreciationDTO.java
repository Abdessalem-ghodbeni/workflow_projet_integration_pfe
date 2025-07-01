package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.*;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppreciationDTO {
    private Long etudiantId;
    private Long tuteurId;
    private Date evaluationDate;
    private TypeAppreciation appreciation;
    private String commentaire;
    private Long projetId;
}
