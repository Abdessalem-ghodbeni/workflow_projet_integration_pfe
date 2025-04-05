package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Epic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEpicRepository extends JpaRepository<Epic,Long> {
}
