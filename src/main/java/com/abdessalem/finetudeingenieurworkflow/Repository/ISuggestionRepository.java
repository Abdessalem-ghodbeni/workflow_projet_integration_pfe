package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISuggestionRepository extends JpaRepository<Suggestion,Long> {
}
