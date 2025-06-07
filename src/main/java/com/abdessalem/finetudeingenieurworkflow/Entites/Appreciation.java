package com.abdessalem.finetudeingenieurworkflow.Entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Table(name = "Appreciation")
public class Appreciation  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "tuteur_id", nullable = false)
    private Tuteur tuteur;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "evaluation_date", nullable = false)
    private Date evaluationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_timestamp", nullable = false, updatable = false)
    private Date creationTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAppreciation valeur;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "projet_id")
    private Projet projet;

    @PrePersist
    protected void onCreate() {
        this.creationTimestamp = new Date();
        if (this.evaluationDate == null) {
            this.evaluationDate = new Date();
        }
    }
}
