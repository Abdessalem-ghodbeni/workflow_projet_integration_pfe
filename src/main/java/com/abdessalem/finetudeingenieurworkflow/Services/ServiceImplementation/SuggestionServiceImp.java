package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Repository.*;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ISuggestionServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuggestionServiceImp implements ISuggestionServices {
    private final ITacheRepository tacheRepository;
    private final IHistoriqueServiceImp historiqueServiceImp;
    private final ISuggestionRepository suggestionRepository;
    private final IUserRepository userRepository;
    public static String uploadDirectory = System.getProperty("user.dir") + "/uploadUser";

    private String saveImage(MultipartFile image) {
        try {
            String filename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDirectory, filename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de l'image", e);
        }
    }
    private void deleteImage(String filename) {
        try {
            Path filePath = Paths.get(uploadDirectory, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {

            throw new RuntimeException("Erreur lors de la supression de l'image", e);
        }
    }
    @Override
    public ApiResponse ajouterSuggestion(Long idTache, String objet, String contenuTexte, MultipartFile image, Long idActeur) {
        Optional<Tache> optionalTache = tacheRepository.findById(idTache);
        if (optionalTache.isEmpty()) {
            return new ApiResponse("Tâche introuvable avec ID : " + idTache, false);
        }

        Tache tache = optionalTache.get();

        Optional<User> optionalUser = userRepository.findById(idActeur);
        if (optionalUser.isEmpty()) {
            return new ApiResponse("Utilisateur introuvable avec ID : " + idActeur, false);
        }

        User utilisateur = optionalUser.get();
        Suggestion.SuggestionBuilder builder = Suggestion.builder()
                .object(objet)
                .contenuTexte(contenuTexte)
                .tache(tache);

        if (image != null && !image.isEmpty()) {
            String savedFilename = saveImage(image);
            builder.image(savedFilename);
        }

        if (utilisateur instanceof Tuteur) {
            builder.tuteur((Tuteur) utilisateur);
        } else if (utilisateur instanceof Societe) {
            builder.societe((Societe) utilisateur);
        } else {
            return new ApiResponse("Seuls les tuteurs ou sociétés peuvent ajouter une suggestion", false);
        }

        suggestionRepository.save(builder.build());

        historiqueServiceImp.enregistrerAction(
                idActeur,
                "Ajout de suggestion",
                "Ajout d'une suggestion pour la tâche ID " + idTache + " par " + utilisateur.getNom()
        );

        return new ApiResponse("Suggestion ajoutée avec succès", true);
    }
}
