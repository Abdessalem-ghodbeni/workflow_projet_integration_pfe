package com.abdessalem.finetudeingenieurworkflow.Entites;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


import lombok.*;
import java.io.Serializable;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Candidature implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectTitle;
    @Column(length = 5000)
    private String motivation;
@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

}
