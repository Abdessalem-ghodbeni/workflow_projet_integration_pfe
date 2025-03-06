package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FormAccessibilityRequest {
    private LocalDateTime dateDebutAccess;
    private LocalDateTime dateFinAccess;
}
