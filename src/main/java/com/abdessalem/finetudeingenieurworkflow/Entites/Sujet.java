package com.abdessalem.finetudeingenieurworkflow.Entites;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @ElementCollection
    @CollectionTable(name = "sujet_technologies", joinColumns = @JoinColumn(name = "sujet_id"))
    @Column(name = "technologies")
    private List<String>  technologies;

    private String thematique;

    private String specialite;
    @Enumerated(EnumType.STRING)
    private Etat etat;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;
@JsonIgnore
// he4i zetha ala 5ter fazt json lezmni ntestyy kn cv n5aleha si no lezm naheha
    @OneToMany(mappedBy = "sujet")
    private List<Projet> projets;
//, nullable = false
    @ManyToOne
    @JoinColumn(name = "tuteur_id")
    private Tuteur tuteur;



    @Column(nullable = false)
    private boolean visibleAuxEtudiants = false;

    @ManyToOne
    @JoinColumn(name = "societe_id")
    private Societe societe;



}
