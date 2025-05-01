package com.abdessalem.finetudeingenieurworkflow.DtoGithub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeScanningAlertDto {
    private Integer number; // ID de l'alerte
    private String ruleId;
    private String severity; // ex: "critical", "high", "warning"
    private String description;
    private String tool; // Outil d'analyse (ex: "CodeQL")
    private Boolean dismissedAt; // Si l'alerte est résolue
    @Data
    public static class Tool {
        private String name; // Nom de l'outil (ex: "CodeQL")
    }
}