package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.Societe;

import java.util.List;


public interface ISocieteServices {
    List<Societe>GetAllSociete();
    Societe recupererById(Long idSociete);


}
