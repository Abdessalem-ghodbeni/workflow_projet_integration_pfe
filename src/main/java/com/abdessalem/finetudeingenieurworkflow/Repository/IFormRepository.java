package com.abdessalem.finetudeingenieurworkflow.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.abdessalem.finetudeingenieurworkflow.Entites.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IFormRepository extends JpaRepository<Form,Long> {
    Page<Form> findByTuteurId(Long tuteurId, Pageable pageable);
    @Query("SELECT f FROM Form f WHERE f.isAccessible = false AND f.dateDebutAccess <= :now AND EXTRACT(YEAR FROM f.dateCreation) = :anneeCourante")
    List<Form> findFormsToActivate(@Param("now") LocalDateTime now, @Param("anneeCourante") int anneeCourante);

    @Query("SELECT f FROM Form f WHERE f.isAccessible = true AND f.dateFinAccess <= :now AND EXTRACT(YEAR FROM f.dateCreation) = :anneeCourante")
    List<Form> findFormsToDeactivate(@Param("now") LocalDateTime now, @Param("anneeCourante") int anneeCourante);



}
