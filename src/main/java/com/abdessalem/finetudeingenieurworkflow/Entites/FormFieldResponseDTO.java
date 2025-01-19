package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FormFieldResponseDTO {
    private Long id;
    private String label;
    private String value;
}
