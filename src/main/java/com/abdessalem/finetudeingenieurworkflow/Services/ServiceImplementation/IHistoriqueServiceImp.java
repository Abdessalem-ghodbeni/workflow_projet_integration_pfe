package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.Historique;
import com.abdessalem.finetudeingenieurworkflow.Entites.User;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.IHistoriqueRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.IUserRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IHistoriqueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IHistoriqueServiceImp implements IHistoriqueService {
    private final IHistoriqueRepository historiqueRepository;
    private final IUserRepository userRepository;

    @Override
    public void enregistrerAction(Long id, String action, String description) {
        User user=userRepository.findById(id).orElseThrow(() -> new RessourceNotFound("utilisateurnon trouvable avec cet id"+id));
        Historique historique = new Historique();
        historique.setUser(user);
        historique.setAction(action);
        historique.setDescription(description);
        historiqueRepository.save(historique);
    }

    @Override
    public List<Historique> historiqueByUser(Long id_user) {
        userRepository.findById(id_user).orElseThrow(() -> new RessourceNotFound("Utlisateur  avec l'ID " + id_user + " n'existe pas."));
        return historiqueRepository.findByUser_IdOrderByDateActionDesc(id_user);
    }

    @Override
    public List<Historique> GetAllHistoriqueList() {
        return historiqueRepository.findAll();
    }
}
