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
public class Projet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;
    @UpdateTimestamp
    private LocalDateTime dateModification;
    @JsonIgnore
    @ManyToOne
    private Sujet sujet;

    @ManyToOne
    private Equipe equipe;

    @OneToOne(cascade = CascadeType.ALL)
    private Backlog backlog;
}
