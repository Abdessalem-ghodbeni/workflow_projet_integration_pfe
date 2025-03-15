package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamMotivationDTO {
    private Long teamId;
    private String teamName;
    private String teamImage;
    private String motivation;
    private double score;
}
