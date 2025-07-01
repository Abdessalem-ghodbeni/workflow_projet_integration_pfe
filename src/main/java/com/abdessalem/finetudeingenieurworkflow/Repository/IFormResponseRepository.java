package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import com.abdessalem.finetudeingenieurworkflow.Entites.Form;
import com.abdessalem.finetudeingenieurworkflow.Entites.FormResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IFormResponseRepository extends JpaRepository<FormResponse,Long> {
    List<FormResponse> findByFormId(Long formId);
    boolean existsByEtudiantAndForm(Etudiant etudiant, Form form);
    Long countByFormId(Long formId);
}
