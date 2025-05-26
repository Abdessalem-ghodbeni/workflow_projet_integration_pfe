package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.ApiResponse;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.AllTimeStats;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.StatsDTO;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.YearStats;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEquipeRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IEtudiantRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ISujetRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITuteurRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.ITuteurServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.FileNameMap;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TuteurServiceImp implements ITuteurServices {
    private final ITuteurRepository tuteurRepository;
    private final IHistoriqueServiceImp historiqueService;
    private final IEtudiantRepository etudiantRepository;
    private final IEquipeRepository equipeRepository;
    private final ISujetRepository sujetRepository;
    @Override
    public List<Tuteur> getAllTuteur() {
        return tuteurRepository.findAll();
    }

    @Override
    public Tuteur getTuteurById(Long id) {
        return tuteurRepository.findById(id).orElseThrow(() -> new RessourceNotFound("Le tuteur avec l'ID " + id + " n'existe pas."));
    }

    @Override
    public Page<Tuteur> getAllTuteurs(Pageable pageable) {
        return tuteurRepository.findAll(pageable);
    }

    @Override
    public Page<Tuteur> searchTuteurs(String keyword, Pageable pageable) {
        return tuteurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword, pageable);
    }

    @Override
    public List<Tuteur> getAllChefOptionsTuteurs() {
        return tuteurRepository.findAllChefOptionsTuteurs();
    }

    @Override
    public ApiResponse toggleChefOption(Long idTuteur, Long idActionneur) {
        Optional<Tuteur> optionalTuteur = tuteurRepository.findById(idTuteur);
        Optional<Tuteur> optionalTuteurActinneur = tuteurRepository.findById(idActionneur);

        if (!optionalTuteur.isPresent()) {
            return new ApiResponse("Tuteur non trouvé avec l'ID : " + idTuteur, false);
        }
        if (!optionalTuteurActinneur.isPresent()) {
            return new ApiResponse("Tuteur non trouvé avec l'ID : " + idActionneur, false);
        }

        Tuteur tuteur = optionalTuteur.get();
        boolean newStatus = !tuteur.is_Chef_Options();
        tuteur.set_Chef_Options(newStatus);
        tuteurRepository.save(tuteur);

        String action = newStatus ? "ACTIVATION_CHEF_OPTION" : "DESACTIVATION_CHEF_OPTION";
        String description = "Changement du statut Chef d'option à : " + newStatus +"du "+tuteur.getNom();
        historiqueService.enregistrerAction(idActionneur, action, description);

        return new ApiResponse("Statut Chef d'option modifié avec succès", true);
    }

    @Override
    public StatsDTO getTuteurStats(Long tuteurId, Integer year) {
        int selectedYear = (year != null) ? year : Year.now().getValue();

        YearStats yearStats = new YearStats();

        // Sujets
        yearStats.setSujetsValides(sujetRepository.countValidatedSujetsByYear(tuteurId, selectedYear));
        yearStats.setSujetsRefuses(sujetRepository.countRejectedSujetsByYear(tuteurId, selectedYear));
        yearStats.setSujetsTuteur(yearStats.getSujetsValides() + yearStats.getSujetsRefuses());
        yearStats.setSujetsPlateforme(sujetRepository.countTotalSujetsByYear(selectedYear));

        // Équipes
        yearStats.setEquipesLieesTuteur(equipeRepository.countEquipesTuteurByYear(tuteurId, selectedYear));
        yearStats.setEquipesPlateforme(equipeRepository.countEquipesPlateformeByYear(selectedYear));

        // Étudiants
        yearStats.setEtudiantsEncadresTuteur(etudiantRepository.countEtudiantsTuteurByYear(tuteurId, selectedYear));
        yearStats.setEtudiantsPlateforme(etudiantRepository.countEtudiantsPlateformeByYear(selectedYear));

        AllTimeStats allTimeStats = new AllTimeStats();

        // Sujets
        allTimeStats.setTotalSujets(sujetRepository.countTotalSujets(tuteurId));
        allTimeStats.setSujetsPlateformeAllTime(sujetRepository.countTotalSujetsAllTime());

        // Équipes
        allTimeStats.setTotalEquipes(equipeRepository.countEquipesTuteurAllTime(tuteurId));
        allTimeStats.setEquipesPlateformeAllTime(equipeRepository.countEquipesPlateformeAllTime());

        // Étudiants
        allTimeStats.setTotalEtudiants(etudiantRepository.countEtudiantsTuteurAllTime(tuteurId));
        allTimeStats.setEtudiantsPlateformeAllTime(etudiantRepository.countEtudiantsPlateformeAllTime());

        return new StatsDTO(selectedYear, yearStats, allTimeStats);
    }

}
