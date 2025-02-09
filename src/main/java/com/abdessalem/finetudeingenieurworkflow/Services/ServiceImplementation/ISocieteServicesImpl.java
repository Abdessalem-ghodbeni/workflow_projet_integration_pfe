package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.Societe;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ISocieteServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ISocieteServicesImpl implements ISocieteServices {
    @Override
    public List<Societe> GetAllSociete() {
        return null;
    }

    @Override
    public Societe recupererById(Long idSociete) {
        return null;
    }
}
