package com.abdessalem.finetudeingenieurworkflow.DtoGithub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryInfoDto {
    @JsonProperty("default_branch") // Nom exact du champ dans la réponse JSON de GitHub
    private String defaultBranch;

    // Autres champs optionnels (non nécessaires pour votre cas d'usage)
    private String name;
    private String full_name;
    private String description;
}
