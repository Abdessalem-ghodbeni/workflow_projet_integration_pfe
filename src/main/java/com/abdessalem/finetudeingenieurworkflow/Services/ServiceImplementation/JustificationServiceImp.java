package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import com.abdessalem.finetudeingenieurworkflow.Entites.Justification;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IJustificationRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITacheRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IJustificationServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class JustificationServiceImp implements IJustificationServices {
    private final ITacheRepository tacheRepository;
    private final IHistoriqueServiceImp historiqueServiceImp;
    private  final IEtudiantRepository etudiantRepository;
    private final IJustificationRepository justificationRepository;
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
    @Override
    @Transactional
    public ApiResponse ajouterJustification(Long idTache, Long idEtudiant, String objet, String contenuTexte, MultipartFile imageFile) {
        Optional<Tache> tacheOpt = tacheRepository.findById(idTache);
        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idEtudiant);

        if (tacheOpt.isEmpty()) {
            return new ApiResponse("Tâche non trouvée", false);
        }

        if (etudiantOpt.isEmpty()) {
            return new ApiResponse("Étudiant non trouvé", false);
        }

        Tache tache = tacheOpt.get();
        Etudiant etudiant = etudiantOpt.get();

        String imageFilename = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageFilename = saveImage(imageFile); // méthode existante dans ton service
        }

        Justification justification = Justification.builder()
                .object(objet)
                .contenuTexte(contenuTexte)
                .image(imageFilename) // juste le nom du fichier
                .tache(tache)
                .etudiant(etudiant)
                .build();

        justificationRepository.save(justification);

        // Historique
        historiqueServiceImp.enregistrerAction(
                idEtudiant,
                "JUSTIFICATION_AJOUTEE",
                "L'étudiant " + etudiant.getNom() + " a ajouté une justification pour la tâche '" + tache.getTitre() + "'"
        );

        return new ApiResponse("Justification ajoutée avec succès", true);
    }

}
