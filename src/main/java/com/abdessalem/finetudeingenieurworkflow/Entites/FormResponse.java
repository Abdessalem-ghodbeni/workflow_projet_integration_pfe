package com.abdessalem.finetudeingenieurworkflow.Entites;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"etudiant_id", "form_id"}))
public class FormResponse implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;
    @OneToMany(mappedBy = "formResponse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormFieldResponse> responses = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;
}
