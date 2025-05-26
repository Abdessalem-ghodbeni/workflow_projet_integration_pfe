package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.StatsDTO;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ITuteurServices {
    List<Tuteur> getAllTuteur();
    Tuteur getTuteurById(Long id);
    Page<Tuteur> getAllTuteurs(Pageable pageable);
    Page<Tuteur> searchTuteurs(String keyword, Pageable pageable);
     List<Tuteur> getAllChefOptionsTuteurs();
    ApiResponse toggleChefOption(Long idTuteur,Long idActionneur);
    StatsDTO getTuteurStats(Long tuteurId, Integer year);

}
