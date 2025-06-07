package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.IAppreciationRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IProjetRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITuteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AppreciationService {
    private final IAppreciationRepository appreciationRepository;
    private final IEtudiantRepository etudiantRepository;
    private final ITuteurRepository tuteurRepository;
    private final IProjetRepository projetRepository;

    @Transactional
    public Appreciation createAppreciation(AppreciationDTO dto) {

        if (dto.getEvaluationDate() != null && dto.getEvaluationDate().after(new Date())) {
            throw new IllegalArgumentException("La date d'évaluation ne peut être dans le futur");
        }
        Etudiant etudiant = etudiantRepository.findById(dto.getEtudiantId())
                .orElseThrow(() -> new RessourceNotFound("Étudiant non trouvé"));

        Tuteur tuteur = tuteurRepository.findById(dto.getTuteurId())
                .orElseThrow(() -> new RessourceNotFound("Tuteur non trouvé"));

        Projet projet = dto.getProjetId() != null ?
                projetRepository.findById(dto.getProjetId())
                        .orElseThrow(() -> new RessourceNotFound("Projet non trouvé")) :
                null;
        return appreciationRepository.save(Appreciation.builder()
                .etudiant(etudiant)
                .tuteur(tuteur)
                .evaluationDate(dto.getEvaluationDate())
                .valeur(dto.getAppreciation())
                .commentaire(dto.getCommentaire())
                .projet(projet)
                .build());
    }
}
