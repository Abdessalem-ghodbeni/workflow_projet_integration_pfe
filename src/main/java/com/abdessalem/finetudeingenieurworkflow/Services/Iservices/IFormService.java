package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.Form;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IFormService {

    Form ajouterForm(Form formulaire,Long idTueteur);
    Page<Form> getFormsByTuteur(Long tuteurId, Pageable pageable);
    List<Form> getAllForms();
    void deleteFormById(Long id,Long idTuteur);
   Form getFormById(Long id);
    Form updateForm(Form updatedForm,Long idTuteur);
}
