package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}
