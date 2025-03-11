package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.*;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IEquipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipeServiceImp implements IEquipeService {

    private final IEquipeRepository equipeRepository;
    private final IEtudiantRepository etudiantRepository;
    private final FormResponseService formResponseService;
    private final ICandidatureRepository candidatureRepository;
    private final IHistoriqueServiceImp historiqueServiceImp;
    private final ITuteurRepository tuteurRepository;
    private final IUserRepository userRepository;


    @Override
    @Transactional
    public ApiResponse construireEquipes(Long formId) {
        List<FormResponseDTO> formResponses = formResponseService.getFormResponses(formId);

        if (formResponses.isEmpty()) {
            return new ApiResponse("Aucune réponse trouvée pour ce formulaire", false);
        }

        int totalAffectes = 0, totalIgnores = 0;

        for (FormResponseDTO formResponse : formResponses) {
            String nomEquipe = null;
            List<String> emailsEtudiants = new ArrayList<>();

            // Maps pour stocker les choix de sujet et les motivations associées
            Map<Integer, String> subjectChoices = new HashMap<>();
            Map<Integer, String> motivationChoices = new HashMap<>();

            // Parcourir toutes les réponses pour extraire les informations
            for (FormFieldResponseDTO response : formResponse.getResponses()) {
                String label = response.getLabel().trim();
                String value = response.getValue().trim();

                if ("Nom de l'équipe".equalsIgnoreCase(label)) {
                    nomEquipe = value;
                } else if (label.toLowerCase().contains("email membre")) {
                    emailsEtudiants.add(value);
                } else if (label.toLowerCase().contains("choisir") && label.toLowerCase().contains("sujet")) {
                    // Extrait le numéro de choix à partir du label (ex: "Choisir 1er sujet" donnera 1)
                    int choixNumero = extraireNumeroChoix(label);
                    if (choixNumero > 0) {
                        subjectChoices.put(choixNumero, value);
                    }
                } else if (label.toLowerCase().contains("motivation") && label.toLowerCase().contains("choix")) {
                    // Extrait le numéro de choix dans le label de la motivation (ex: "Motivation 1er choix")
                    int choixNumero = extraireNumeroChoix(label);
                    if (choixNumero > 0) {
                        motivationChoices.put(choixNumero, value);
                    }
                }
            }

            // On vérifie la présence des informations essentielles
            if (nomEquipe == null || emailsEtudiants.isEmpty()) {
                continue;
            }

            // Création ou récupération de l'équipe
            String finalNomEquipe = nomEquipe;
            Equipe equipe = equipeRepository.findByNom(nomEquipe)
                    .orElseGet(() -> equipeRepository.save(Equipe.builder().nom(finalNomEquipe).build()));

            // Initialiser la liste des candidatures si nécessaire
            if (equipe.getCandidatures() == null) {
                equipe.setCandidatures(new ArrayList<>());
            }

            // Créer les candidatures en associant chaque sujet à sa motivation
            for (Map.Entry<Integer, String> entry : subjectChoices.entrySet()) {
                int choixNumero = entry.getKey();
                String subject = entry.getValue();
                String motivation = motivationChoices.get(choixNumero);
                if (motivation != null && !motivation.isEmpty()) {
                    Candidature candidature = Candidature.builder()
                            .subjectTitle(subject)
                            .motivation(motivation)
                            .equipe(equipe)
                            .build();
                    equipe.getCandidatures().add(candidature);
                }
            }

            equipeRepository.save(equipe);

            // Affecter les étudiants à l'équipe
            int affectes = 0, ignores = 0;
            for (String email : emailsEtudiants) {
                Optional<Etudiant> etudiantOpt = etudiantRepository.findByEmail(email);
                if (etudiantOpt.isPresent()) {
                    Etudiant etudiant = etudiantOpt.get();
                    if (etudiant.getEquipe() == null) {
                        etudiant.setEquipe(equipe);
                        etudiantRepository.save(etudiant);
                        affectes++;
                    } else {
                        ignores++;
                    }
                }
            }

            totalAffectes += affectes;
            totalIgnores += ignores;
        }

        return new ApiResponse(
                String.format("Équipes créées avec succès. %d étudiants affectés, %d ignorés (déjà dans une équipe).", totalAffectes, totalIgnores),
                true
        );
    }

    @Override
    @Transactional
    public ApiResponse ajouterEtudiantAEquipe(Long etudiantId, Long equipeId, Long userId) {
        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(etudiantId);
        Optional<Equipe> equipeOpt = equipeRepository.findById(equipeId);


        User utilisateur = userRepository.findById(userId)
                .orElseThrow(() -> new RessourceNotFound("Utilisateur non trouvé"));

        if (etudiantOpt.isPresent() && equipeOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();
            Equipe equipe = equipeOpt.get();


            if (etudiant.getEquipe() != null) {
                return new ApiResponse(
                        "L'étudiant est déjà assigné à une équipe.",
                        false
                );
            }


            etudiant.setEquipe(equipe);
            equipe.getEtudiants().add(etudiant);

            // Sauvegarder les modifications
            etudiantRepository.save(etudiant);
            equipeRepository.save(equipe);


            if (utilisateur instanceof Tuteur) {
                historiqueServiceImp.enregistrerAction(userId, "MODIFICATION",
                        utilisateur.getNom() + " a ajouté un étudiant à l'équipe " + equipe.getNom());
            } else {
                historiqueServiceImp.enregistrerAction(etudiantId, "MODIFICATION",
                        utilisateur.getNom() + " a rejoint l'équipe " + equipe.getNom());
            }

            return new ApiResponse(
                    "L'étudiant a été ajouté à l'équipe avec succès.",
                    true
            );

        } else {
            return new ApiResponse(
                    "Étudiant ou équipe introuvable.",
                    false
            );
        }
    }


    @Override
    @Transactional
    public ApiResponse retirerEtudiantDeEquipe(Long userId, Long etudiantId) {

        User utilisateur = userRepository.findById(userId)
                .orElseThrow(() -> new RessourceNotFound("Utilisateur non trouvé"));

        // Vérification de l'existence de l'étudiant
        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(etudiantId);
        if (etudiantOpt.isEmpty()) {

            return new ApiResponse(
                    String.format("Étudiant introuvable."),
                    false
            );
        }

        Etudiant etudiant = etudiantOpt.get();

        // Vérifier si l'étudiant est assigné à une équipe
        if (etudiant.getEquipe() == null) {

            return new ApiResponse(
                    String.format("L'étudiant n'est pas assigné à une équipe."),
                    false
            );

        }

        Equipe equipe = etudiant.getEquipe();
        equipe.getEtudiants().remove(etudiant);
        etudiant.setEquipe(null);

        etudiantRepository.save(etudiant);
        equipeRepository.save(equipe);


        if (utilisateur instanceof Tuteur) {
            historiqueServiceImp.enregistrerAction(userId, "Modification",
                  utilisateur.getNom()+  "a retiré l'étudiant"+ etudiant.getNom()+ " de l'équipe " + equipe.getNom());
        } else {
            historiqueServiceImp.enregistrerAction(etudiantId, "Modification",

            utilisateur.getNom()+  "a quitté l'équipe " + equipe.getNom());
        }


        return new ApiResponse(
                String.format("L'étudiant a été retiré de l'équipe avec succès."),
                true
        );
    }

    @Override
    public List<Equipe> getEquipesBySpecialiteAndCurrentYear(String specialite) {
        return equipeRepository.findEquipesBySpecialiteAndCurrentYear(specialite);
    }


    private int extraireNumeroChoix(String label) {
        String number = label.replaceAll("\\D+", "");
        return number.isEmpty() ? 0 : Integer.parseInt(number);
    }



@Transactional
    public List<Equipe> getAllEquipe(){
        return equipeRepository.findAll();
}

@Transactional
    public List<Equipe> recupererListeEquipeByIdFormulaire(Long formId){
        return equipeRepository.findEquipesByFormId(formId);
}

}
