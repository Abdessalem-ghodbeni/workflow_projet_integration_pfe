package com.abdessalem.finetudeingenieurworkflow.Entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiltrageVisibleSubjectDTO {
    private List<String> titres;
    private List<String> thematiques;
}
