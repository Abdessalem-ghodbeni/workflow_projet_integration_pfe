package com.abdessalem.finetudeingenieurworkflow.Entites;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

@Table(name = "Instructor")
public class Instructor extends User implements Serializable{

    @Column(name = "is_Chef_Options")
    private boolean is_Chef_Options=false;
    @Column(name="specialite_up")
    private String specialiteUp;
    @Column(name="nationality")
    private String nationality;
    private String image;

    @Temporal(TemporalType.DATE)
    private Date dateEmbauche;

/// relations
@OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Sujet> sujets;

}
