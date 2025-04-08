package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Backlog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBacklogRepository extends JpaRepository<Backlog,Long> {
}
