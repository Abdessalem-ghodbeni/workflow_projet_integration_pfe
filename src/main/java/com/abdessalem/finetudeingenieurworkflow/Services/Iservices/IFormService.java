package com.abdessalem.finetudeingenieurworkflow.Services.Iservices;

import com.abdessalem.finetudeingenieurworkflow.Entites.Form;

import java.util.List;

public interface IFormService {

    Form ajouterForm(Form formulaire);
    List<Form> getAllForms();

}
