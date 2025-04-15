package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IJustificationServices {
    ApiResponse ajouterJustification(Long idTache, Long idEtudiant, String objet, String contenuTexte, MultipartFile imageFile);
    ApiResponse modifierJustification(Long idJustification, Long idEtudiant, String nouvelObjet, String nouveauContenuTexte, MultipartFile nouvelleImage);
}
