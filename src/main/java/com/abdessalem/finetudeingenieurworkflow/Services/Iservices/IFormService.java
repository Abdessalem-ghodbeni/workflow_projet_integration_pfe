package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.Form;

import java.util.List;
import java.util.Optional;

public interface IFormService {

    Form ajouterForm(Form formulaire);
    List<Form> getAllForms();
    void deleteFormById(Long id);
   Form getFormById(Long id);
}
