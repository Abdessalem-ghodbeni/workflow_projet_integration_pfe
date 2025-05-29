package com.abdessalem.finetudeingenieurworkflow.Repository;
import com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.FormStatsDTO;
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

    @Query("SELECT f FROM Form f WHERE f.isAccessible = true " +
            "AND EXTRACT(YEAR FROM f.dateCreation) = :annee " +
            "AND f.tuteur.specialiteUp = :specialiteUp")
    List<Form> findVisibleFormsForStudents(@Param("annee") int annee, @Param("specialiteUp") String specialiteUp);


    // API 1: Stats globales
    @Query("SELECT " +
            "NEW com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.FormStatsDTO(f.id, f.titre, COUNT(fr.id)) " +
            "FROM Form f " +
            "LEFT JOIN FormResponse fr ON f.id = fr.form.id " +
            "GROUP BY f.id")
    List<FormStatsDTO> getFormsStats();

    // API 2: Stats par tuteur
    @Query("SELECT " +
            "NEW com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique.FormStatsDTO(f.id, f.titre, COUNT(fr.id)) " +
            "FROM Form f " +
            "LEFT JOIN FormResponse fr ON f.id = fr.form.id " +
            "WHERE f.tuteur.id = :tuteurId " +
            "GROUP BY f.id")
    List<FormStatsDTO> getFormsStatsByTuteur(@Param("tuteurId") Long tuteurId);

}
