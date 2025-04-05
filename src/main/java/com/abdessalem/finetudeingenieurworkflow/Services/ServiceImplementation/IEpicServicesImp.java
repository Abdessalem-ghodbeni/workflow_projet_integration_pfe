package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;
import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import com.abdessalem.finetudeingenieurworkflow.Entites.Projet;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEpicRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IProjetRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISujetRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IEpicServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class IEpicServicesImp implements IEpicServices {
    private final IProjetRepository projetRepository;
    private final IHistoriqueServiceImp historiqueServiceImp;
    private final IEtudiantRepository etudiantRepository;
    private final IEpicRepository epicRepository;
    @Override
    public ApiResponse addEpicToProject(Long projetId, Long etudiantId, Epic epic) {

        Optional<Projet> projetOpt = projetRepository.findById(projetId);
        if (!projetOpt.isPresent()) {
            return new ApiResponse("Projet non trouvé", false);
        }
        Projet projet = projetOpt.get();

        epic.setProjet(projet);
        epicRepository.save(epic);

        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(etudiantId);
        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();
            historiqueServiceImp.enregistrerAction(etudiantId, "MODIFICATION",
                    etudiant.getNom() + " a ajouté l'épic '" + epic.getNom() + "' au projet '" + projet.getNom() + "'");
        }

        return new ApiResponse("Epic ajouté avec succès", true);

    }
}
