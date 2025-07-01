package com.abdessalem.finetudeingenieurworkflow.Entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Table(name = "Etudiant")
public class Etudiant extends User implements Serializable {

    @Column(name="niveau")
    private long niveau;
    @Column(name="classe")
    private String classe;

    @Column(name="specialite")
    private String specialite;
    @Column(name="nationality")
    private String nationality;
    private String image;

    @Temporal(TemporalType.DATE)
    private Date dateNaissance;
    @JsonIgnore
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormResponse> formResponses = new ArrayList<>();
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;
    @JsonIgnore
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Justification> justifications = new ArrayList<>();
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Appreciation> appreciations = new ArrayList<>();
}
