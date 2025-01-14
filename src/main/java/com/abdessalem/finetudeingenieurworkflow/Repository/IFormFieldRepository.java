package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.FormField;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFormFieldRepository extends JpaRepository<FormField,Long> {
}
