package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.Societe;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISocieteRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ISocieteServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ISocieteServicesImpl implements ISocieteServices {
    private final ISocieteRepository societeRepository;
    @Override
    public List<Societe> GetAllSociete() {
        List<Societe> listeSociete=societeRepository.findAll();
        return listeSociete;
    }

    @Override
    public Societe recupererById(Long idSociete) {
        return societeRepository.findById(idSociete).orElseThrow(() -> new RessourceNotFound("societe avec l'ID " + idSociete + " n'existe pas."));
    }

    @Override
    public Page<Societe> getAllSocietes(Pageable pageable) {
        return societeRepository.findAll(pageable);
    }

    @Override
    public Page<Societe> searchSocietes(String keyword, Pageable pageable) {
        return societeRepository.findByNomContainingIgnoreCaseOrSecteurActiviteContainingIgnoreCase(keyword, keyword, pageable);
    }

    @Override
    public Page<Societe> getFilteredSocietes(List<String> secteurs, List<String> villes, Boolean active, Pageable pageable) {
        return societeRepository.findFilteredSocietes(secteurs, villes, active, pageable);
    }


}
