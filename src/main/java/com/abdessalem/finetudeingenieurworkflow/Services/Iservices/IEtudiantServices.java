package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;

import java.util.List;

public interface IEtudiantServices {


    List<Etudiant> recuperListeEtudiant();
    Etudiant getEtudiantById(Long id);


}
