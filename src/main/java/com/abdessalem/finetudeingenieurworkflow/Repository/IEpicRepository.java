package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEpicRepository extends JpaRepository<Epic,Long> {
    List<Epic> findByProjetId(Long projetId);
}
