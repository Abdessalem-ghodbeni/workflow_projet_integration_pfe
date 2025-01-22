package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITuteurRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITuteurServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TuteurServiceImp implements ITuteurServices {
    private final ITuteurRepository tuteurRepository;
    @Override
    public List<Tuteur> getAllTuteur() {
        return tuteurRepository.findAll();
    }

    @Override
    public Tuteur getTuteurById(Long id) {
        return tuteurRepository.findById(id).orElseThrow(() -> new RessourceNotFound("Le tuteur avec l'ID " + id + " n'existe pas."));
    }
}
