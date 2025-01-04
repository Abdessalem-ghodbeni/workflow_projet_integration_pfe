package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInstructorRepository extends JpaRepository<Instructor,Long> {
}
