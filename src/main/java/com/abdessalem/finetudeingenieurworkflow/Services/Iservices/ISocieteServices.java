package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.Societe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;


public interface ISocieteServices {
    List<Societe>GetAllSociete();
    Societe recupererById(Long idSociete);
    Page<Societe> getAllSocietes(Pageable pageable);
    Page<Societe> searchSocietes(String keyword, Pageable pageable);

    Page<Societe> getFilteredSocietes(List<String> secteurs, List<String> villes, Boolean active, Pageable pageable);
}
