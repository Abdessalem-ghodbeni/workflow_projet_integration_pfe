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
import java.util.List;
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
    private void deleteImage(String filename) {
        try {
            Path filePath = Paths.get(uploadDirectory, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log ou ignore en fonction de ta politique
            System.err.println("Erreur lors de la suppression de l'image : " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse ajouterJustification(Long idTache, Long idEtudiant, String object, String contenuTexte, MultipartFile imageFile) {
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
            imageFilename = saveImage(imageFile);
        }

        Justification justification = Justification.builder()
                .object(object)
                .contenuTexte(contenuTexte)
                .image(imageFilename)
                .tache(tache)
                .etudiant(etudiant)
                .build();

        justificationRepository.save(justification);


        historiqueServiceImp.enregistrerAction(
                idEtudiant,
                "JUSTIFICATION_AJOUTEE",
                "L'étudiant " + etudiant.getNom() + " a ajouté une justification pour la tâche '" + tache.getTitre() + "'"
        );

        return new ApiResponse("Justification ajoutée avec succès", true);
    }

    @Override
    public ApiResponse modifierJustification(Long idJustification, Long idEtudiant, String nouvelObjet, String nouveauContenuTexte, MultipartFile nouvelleImage) {
        {
            Optional<Justification> justificationOpt = justificationRepository.findById(idJustification);
            Optional<Etudiant> etudiantOpt = etudiantRepository.findById(idEtudiant);

            if (justificationOpt.isEmpty()) {
                return new ApiResponse("Justification non trouvée", false);
            }

            if (etudiantOpt.isEmpty()) {
                return new ApiResponse("Étudiant non trouvé", false);
            }

            Justification justification = justificationOpt.get();
            Etudiant etudiant = etudiantOpt.get();

            // Vérifier que c'est bien l'étudiant qui a créé la justification (sécurité)
            if (!justification.getEtudiant().getId().equals(idEtudiant)) {
                return new ApiResponse("Vous n'êtes pas autorisé à modifier cette justification", false);
            }

            // Mise à jour des champs
            justification.setObject(nouvelObjet);
            justification.setContenuTexte(nouveauContenuTexte);

            // Mise à jour de l'image si nouvelle image envoyée
            if (nouvelleImage != null && !nouvelleImage.isEmpty()) {
                // Supprimer l'ancienne image si elle existe
                if (justification.getImage() != null) {
                    deleteImage(justification.getImage());
                }

                // Sauvegarde de la nouvelle image
                String nouvelleImageNom = saveImage(nouvelleImage);
                justification.setImage(nouvelleImageNom);
            }

            justificationRepository.save(justification);

            // Historique
            historiqueServiceImp.enregistrerAction(
                    idEtudiant,
                    "JUSTIFICATION_MODIFIEE",
                    "L'étudiant " + etudiant.getNom() + " a modifié une justification (ID : " + justification.getId() + ") pour la tâche '" + justification.getTache().getTitre() + "'"
            );

            return new ApiResponse("Justification modifiée avec succès", true);
        }

    }

    @Override
    public List<Justification> findByTacheId(Long tacheId) {
        return justificationRepository.findByTacheId(tacheId);
    }

    @Override
    public Justification getJustificationById(Long id) {
        return justificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Justification non trouvée avec l'ID : " + id));
    }


}
