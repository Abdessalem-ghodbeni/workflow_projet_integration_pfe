package com.abdessalem.finetudeingenieurworkflow.Entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "Societe")
public class Societe extends User implements Serializable {
    @Column(name = "adresse")
    private String adresse;

    @Column(name = "ville")
    private String ville;

    @Column(name = "code_postal")
    private String codePostal;

    @Column(name = "pays")
    private String pays;

    @Column(name = "secteur_activite")
    private String secteurActivite;

    @Column(name = "site_web")
    private String siteWeb;

    @Column(name = "logo")
    private String logo;

    @Column(name = "is_active")
    private boolean isActive = true;
}
