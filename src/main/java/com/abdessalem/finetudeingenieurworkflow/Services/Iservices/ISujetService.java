package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;

import java.util.List;

public interface ISujetService {
//    Sujet createSujet(Long utilisateurId, Sujet sujet);
Sujet createSujet( Sujet sujet);
    Sujet getSujetById(Long id);
    void deleteSujet(Long id);
    List<Sujet> getAllSujets();
    Sujet updateSujet(Sujet sujet);
}
