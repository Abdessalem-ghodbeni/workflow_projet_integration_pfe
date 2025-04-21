package com.abdessalem.finetudeingenieurworkflow.Entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Suggestion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "object")
    private String object;
    @Column(columnDefinition = "TEXT")
    private String contenuTexte;
    private String image;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;
    @UpdateTimestamp
    private LocalDateTime dateModification;
    @JsonIgnore
    @ManyToOne
    private Tache tache;

    @ManyToOne
    private Tuteur tuteur;

    @ManyToOne
    private Societe societe;

}
