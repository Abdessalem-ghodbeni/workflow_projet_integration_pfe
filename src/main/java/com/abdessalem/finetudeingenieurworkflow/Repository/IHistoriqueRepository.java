package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Historique;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IHistoriqueRepository extends JpaRepository<Historique,Long> {
    List<Historique> findByUser_IdOrderByDateActionDesc(Long userId);
}
