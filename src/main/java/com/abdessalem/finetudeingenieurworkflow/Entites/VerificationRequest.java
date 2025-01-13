package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.Data;

@Data
public class VerificationRequest {
    private String email;
    private Integer code;
}
