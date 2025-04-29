package com.abdessalem.finetudeingenieurworkflow.DtoGithub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class PullRequestDto {
    private int number;
    @JsonProperty("mergeable")
    private boolean mergeable; // Ajouter ce champ
    @JsonProperty("merged_at")
    private String mergedAt;
}

