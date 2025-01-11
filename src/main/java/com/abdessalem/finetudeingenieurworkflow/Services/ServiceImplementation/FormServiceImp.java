package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.Form;
import com.abdessalem.finetudeingenieurworkflow.Entites.FormField;
import com.abdessalem.finetudeingenieurworkflow.Exception.RessourceNotFound;
import com.abdessalem.finetudeingenieurworkflow.Repository.IFormField;
import com.abdessalem.finetudeingenieurworkflow.Repository.IFormRepository;
import com.abdessalem.finetudeingenieurworkflow.Services.Iservices.IFormService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FormServiceImp implements IFormService {
    private final IFormRepository formRepository;
    private final IFormField formFieldRepository;
    @Override
    public Form ajouterForm(Form formulaire) {
        for (FormField formField : formulaire.getFormFields()) {
            formField.setForm(formulaire); // Lier le FormField au Form
        }

        // Sauvegarder le formulaire (les FormField sont sauvegardés grâce à CascadeType.ALL)
        Form savedForm = formRepository.save(formulaire);

        log.info("Form and associated FormFields saved successfully: {}", savedForm);
        return savedForm;
    }

    @Override
    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    @Override
    public void deleteFormById(Long id) {
        if (formRepository.existsById(id)) {
            formRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Form with ID " + id + " does not exist.");
        }
    }

    @Override
    public Form getFormById(Long id) {
        return formRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFound("Le formulaire avec l'ID " + id + " n'existe pas."));
    }
}
