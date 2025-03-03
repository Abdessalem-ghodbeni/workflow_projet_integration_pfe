package com.abdessalem.finetudeingenieurworkflow.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.abdessalem.finetudeingenieurworkflow.Entites.Form;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFormRepository extends JpaRepository<Form,Long> {
    Page<Form> findByTuteurId(Long tuteurId, Pageable pageable);
}
