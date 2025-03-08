package com.abdessalem.finetudeingenieurworkflow.Entites;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Builder
@Getter
@Setter
public class FormResponseDTO {
    private Long id;

    private List<FormFieldResponseDTO> responses;
}
