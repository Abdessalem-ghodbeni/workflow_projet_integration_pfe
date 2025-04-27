package com.abdessalem.finetudeingenieurworkflow.DtoGithub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PullRequestDto {
//    private String url;
//    private String merged_at;
//    private int number;

    /** Numéro de la PR */
    private int number;

    /** URL de la PR (optionnel si tu en as besoin) */
    private String url;

    /** Date de merge (null si non mergée) */
    @JsonProperty("merged_at")
    private String mergedAt;
}
