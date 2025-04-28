package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.Data;

@Data
public class CodacyAnalysisResponse {
    private int code_smells;
    private int bugs;
    private int duplications;
    private double coverage;
}
