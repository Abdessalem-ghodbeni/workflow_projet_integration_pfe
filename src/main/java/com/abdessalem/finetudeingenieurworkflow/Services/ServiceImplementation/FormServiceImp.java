package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.*;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.IFormField;
import com.abdessalem.finetudeingenieurworkflow.Repository.IFormRepository;
import com.abdessalem.finetudeingenieurworkflow.Repository.ITuteurRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IFormService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FormServiceImp implements IFormService {
    private final IFormRepository formRepository;
    private final IFormField formFieldRepository;
    private final ITuteurRepository tuteurRepository;
    private final IHistoriqueServiceImp historiqueServiceImp;
    @Override
    @Transactional
    public Form ajouterForm(Form formulaire,Long idTuteur) {
        Tuteur tuteur = tuteurRepository.findById(idTuteur)
                .orElseThrow(() -> new RessourceNotFound("tuteur non trouvé"));
        for (FormField formField : formulaire.getFormFields()) {
            formField.setForm(formulaire);
        }
        formulaire.setTuteur(tuteur);
        tuteur.getForms().add(formulaire);
        Form savedForm = formRepository.save(formulaire);
        historiqueServiceImp.enregistrerAction(idTuteur, "CREATION",
                "a ajouté  un sujet dont leur numéro est  " + savedForm.getId());
        return savedForm;
    }

    @Override
    public Page<Form> getFormsByTuteur(Long tuteurId, Pageable pageable) {
        return formRepository.findByTuteurId(tuteurId, pageable);
    }

    @Override
    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    @Override
    public void deleteFormById(Long id,Long idTuteur) {
        Tuteur tuteur = tuteurRepository.findById(idTuteur)
                .orElseThrow(() -> new RessourceNotFound("tuteur non trouvé"));

        if (formRepository.existsById(id)) {
            formRepository.deleteById(id);
            historiqueServiceImp.enregistrerAction(idTuteur,"Suppresion",
                    tuteur.getNom()+"a Supprimé  un formulaire dont leur numéro est  " + id);
        } else {
            throw new IllegalArgumentException("Form with ID " + id + " does not exist.");
        }
    }

    @Override
    public Form getFormById(Long id) {
        return formRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFound("Le formulaire avec l'ID " + id + " n'existe pas."));
    }

    @Override
    @Transactional
    public Form updateForm(Form updatedForm,Long idTuteur) {

        Form existingForm = formRepository.findById(updatedForm.getId())
                .orElseThrow(() -> new RessourceNotFound("Formulaire avec l'ID " + updatedForm.getId() + " non trouvé"));
        Tuteur tuteur = tuteurRepository.findById(idTuteur)
                .orElseThrow(() -> new RessourceNotFound("tuteur non trouvé"));

        existingForm.setTitre(updatedForm.getTitre());
        existingForm.setDescription(updatedForm.getDescription());
        existingForm.setColor(updatedForm.getColor());


        List<FormField> updatedFields = updatedForm.getFormFields();
        List<FormField> existingFields = existingForm.getFormFields();


        existingFields.removeIf(existingField ->
                updatedFields.stream().noneMatch(updatedField -> updatedField.getId() != null && updatedField.getId().equals(existingField.getId()))
        );


        for (FormField updatedField : updatedFields) {
            if (updatedField.getId() == null) {

                updatedField.setForm(existingForm);
                existingFields.add(updatedField);
            } else {

                existingFields.stream()
                        .filter(existingField -> existingField.getId().equals(updatedField.getId()))
                        .findFirst()
                        .ifPresent(existingField -> {
                            existingField.setType(updatedField.getType());
                            existingField.setLabel(updatedField.getLabel());
                            existingField.setIcon(updatedField.getIcon());
                            existingField.setPlaceholder(updatedField.getPlaceholder());
                            existingField.setRequired(updatedField.isRequired());
                            existingField.setOptions(updatedField.getOptions());
                            existingField.setIndex(updatedField.getIndex());
                        });
            }
        }

        historiqueServiceImp.enregistrerAction(idTuteur,"Modification",
                tuteur.getNom()+"a modifié  un formulaire dont leur numéro est  " + existingForm.getId());
        return formRepository.save(existingForm);

    }

    @Override
    public ApiResponse setFormAccessibility(Long formId, Long idTuteur, FormAccessibilityRequest request) {
        Tuteur tuteur = tuteurRepository.findById(idTuteur)
                .orElseThrow(() -> new RessourceNotFound("tuteur non trouvé"));
        Optional<Form> formOptional = formRepository.findById(formId);
        if (formOptional.isEmpty()) {
            return new ApiResponse("Formulaire introuvable !", false);
        }

        Form form = formOptional.get();
        form.setDateDebutAccess(request.getDateDebutAccess());
        form.setDateFinAccess(request.getDateFinAccess());

        formRepository.save(form);
        historiqueServiceImp.enregistrerAction(idTuteur,"Modification",
                tuteur.getNom()+"a définit durée d'accessibilité de formulaire de collecte données dont leur numéro est  " + formId);
        return new ApiResponse("Période d'accessibilité définie avec succès !", true);
    }

    @Override
    public List<Form> getVisibleFormsForStudents(String specialite) {
        int anneeCourante = Year.now().getValue();
        return formRepository.findVisibleFormsForStudents(anneeCourante, specialite);
    }

    @Transactional
    @Scheduled(fixedRate = 50000)
    public void updateFormAccessibility() {
        LocalDateTime now = LocalDateTime.now();
        int anneeCourante = Year.now().getValue();


        List<Form> formsToActivate = formRepository.findFormsToActivate(now, anneeCourante);
        for (Form form : formsToActivate) {
            form.setAccessible(true);
            log.info("Activation du formulaire: {}", form.getId());
        }


        List<Form> formsToDeactivate = formRepository.findFormsToDeactivate(now, anneeCourante);
        for (Form form : formsToDeactivate) {
            form.setAccessible(false);
            log.info("Désactivation du formulaire: {}", form.getId());
        }
        formRepository.saveAll(formsToActivate);
        formRepository.saveAll(formsToDeactivate);

    }

}
