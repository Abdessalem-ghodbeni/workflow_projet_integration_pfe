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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Tache implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    @Enumerated(EnumType.STRING)
    private Complexity complexite;
    @Enumerated(EnumType.STRING)
    private Priorite priorite;
    @Column(nullable = false)
    private boolean estAffecteeAuSprint=false;
    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
    private EtatTache etat = EtatTache.TO_DO;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;
    @JsonIgnore
    @ManyToOne
    private Backlog backlog;
    @ManyToOne
    private Epic epic;
    @JsonIgnore
    @ManyToOne
    private Sprint sprint;
    @ManyToOne
    private Etudiant etudiant;

    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Justification> justifications;

    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Suggestion> suggestions;
    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EtatHistoriqueTache> historiqueEtats;

    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodeAnalysisResult> analyses;

    public Optional<CodeAnalysisResult> getDerniereAnalyse() {
        if (analyses == null || analyses.isEmpty()) return Optional.empty();
        return analyses.stream()
                .max(Comparator.comparing(CodeAnalysisResult::getDateDerniereAnalyseGit));
    }


}
