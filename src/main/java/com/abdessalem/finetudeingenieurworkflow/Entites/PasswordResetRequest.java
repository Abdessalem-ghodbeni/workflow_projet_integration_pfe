package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String email;
    private String newPassword;
    private String token;
}
