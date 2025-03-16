package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IEtudiantServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IEtudiantServicesImp implements IEtudiantServices {
    private final IEtudiantRepository etudiantRepository;

    @Override
    public List<Etudiant> recuperListeEtudiant() {
        return etudiantRepository.findAll();
    }

    @Override
    public Etudiant getEtudiantById(Long id) {
        return etudiantRepository.findById(id).orElseThrow(() -> new RessourceNotFound("L etudiant avec l'ID " + id + " n'existe pas."));
    }

    @Override
    public List<Etudiant> getEtudiantsBySpecialite(String specialite) {
       return etudiantRepository.findBySpecialite(specialite);
    }
}
