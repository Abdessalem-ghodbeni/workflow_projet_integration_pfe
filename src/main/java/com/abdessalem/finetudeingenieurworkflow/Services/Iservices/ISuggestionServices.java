package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ISuggestionServices {
    ApiResponse ajouterSuggestion(Long idTache, String objet, String contenuTexte, MultipartFile image, Long idActeur);
}
