package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.Historique;

import java.util.List;

public interface IHistoriqueService {
     void enregistrerAction(Long id, String action, String description);
    List<Historique> historiqueByUser(Long id_user);

    List<Historique> GetAllHistoriqueList();

}
