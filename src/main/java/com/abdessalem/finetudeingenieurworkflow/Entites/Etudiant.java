package com.abdessalem.finetudeingenieurworkflow.Entites;

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
//    @Column(name = "is_active")
//    private boolean isActive = true;



}
