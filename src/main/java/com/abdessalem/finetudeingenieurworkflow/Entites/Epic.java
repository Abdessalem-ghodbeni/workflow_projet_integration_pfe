package com.abdessalem.finetudeingenieurworkflow.Entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Epic implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private LocalDate dateEstimation;

    private Integer complexite;

    private String couleur;
    @JsonIgnore
    @OneToMany(mappedBy = "epic")
    private List<Tache> taches;

    // Liaison directe avec le projet
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "projet_id")
    private Projet projet;

    @PrePersist
    public void prePersist() {
        if (couleur == null || couleur.isEmpty()) {
            couleur = generateRandomColor();
        }
    }

    private String generateRandomColor() {

        Random random = new Random();
        int red = 150 + random.nextInt(106);
        int green = random.nextInt(101);
        int blue = random.nextInt(101);


        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
