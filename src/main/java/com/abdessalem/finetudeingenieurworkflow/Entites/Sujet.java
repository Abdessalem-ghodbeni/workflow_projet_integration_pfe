package com.abdessalem.finetudeingenieurworkflow.Entites;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Sujet implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "sujet_exigences", joinColumns = @JoinColumn(name = "sujet_id"))
    @Column(name = "exigence")
    private List<String> exigences;

    private String technologie;

    private String thematique;

    private String specialite;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "sujet")
    private List<Projet> projets;
//, nullable = false
    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Tuteur utilisateur;
}
