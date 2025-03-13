package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.*;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IEquipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;


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
    public static String uploadDirectory = System.getProperty("user.dir") + "/uploadUser";

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
                    .orElseGet(() -> {
                        try {
                            String imageFilename = generateInitialsAvatar(finalNomEquipe);
                            return equipeRepository.save(Equipe.builder()
                                    .nom(finalNomEquipe)
                                    .image(imageFilename)
                                    .etat(EtatEquipe.PENDING)
                                    .build());
                        } catch (IOException e) {
                            throw new RuntimeException("Erreur lors de la génération de l'image", e);
                        }
                    });

            if (equipe.getCandidatures() == null) {
                equipe.setCandidatures(new ArrayList<>());
            }

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

    private String generateInitialsAvatar(String nomEquipe) throws IOException {
        int width = 200;
        int height = 200;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        Color color1 = Color.RED;
        Color color2 = Color.BLACK;
        GradientPaint gradient = new GradientPaint(0, 0, color1, width, height, color2, true);
        graphics.setPaint(gradient);
        graphics.fillRect(0, 0, width, height);

        String initials = nomEquipe.length() >= 2 ? nomEquipe.substring(0, 2).toUpperCase() : nomEquipe.toUpperCase();
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", Font.BOLD, 100));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int x = (width - fontMetrics.stringWidth(initials)) / 2;
        int y = ((height - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();
        graphics.drawString(initials, x, y);

        graphics.dispose();

        String filename = UUID.randomUUID().toString() + "_avatar.png";
        Path filePath = Paths.get(uploadDirectory, filename);
        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        ImageIO.write(image, "png", filePath.toFile());
        return filename;
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
    public List<Etudiant> getEquipesBySpecialiteAndCurrentYear(String specialite) {
        return equipeRepository.findBySpecialiteAndCurrentYear(specialite);
    }

    @Override
    public ApiResponse changerStatutEquipe(Long equipeId,Long tuteurId, EtatEquipe nouveauStatut) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée"));
        Tuteur tuteur = tuteurRepository.findById(tuteurId)
                .orElseThrow(() -> new RuntimeException("tuteur non trouvé"));
        equipe.setEtat(nouveauStatut);
        equipeRepository.save(equipe);
        historiqueServiceImp.enregistrerAction(tuteurId, "Modification",

                tuteur.getNom()+  "a modifier le status  l'équipe " + equipe.getNom());
        return new ApiResponse(
                String.format("Le statut de l'équipe '%s' a été changé à '%s'.", equipe.getNom(), nouveauStatut),
                true
        );
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
