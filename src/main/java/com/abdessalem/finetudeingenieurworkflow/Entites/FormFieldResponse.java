package com.abdessalem.finetudeingenieurworkflow.Entites;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FormFieldResponse implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; private String value;
    @ManyToOne
    @JoinColumn(name = "form_field_id")
    private FormField formField;
    @ManyToOne
    @JoinColumn(name = "form_response_id")
    private FormResponse formResponse;
}
