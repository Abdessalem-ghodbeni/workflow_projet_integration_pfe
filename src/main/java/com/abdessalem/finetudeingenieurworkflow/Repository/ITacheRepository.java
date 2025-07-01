package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.EtatTache;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface ITacheRepository extends JpaRepository<Tache,Long> {
    @Query("SELECT t FROM Tache t " +
            "JOIN t.sprint s " +
            "JOIN s.projet p " +
            "WHERE t.dateFinEstimee < :today " +
            "AND t.etat NOT IN :completedStates " +
            "AND t.notified = false " +
            "AND YEAR(t.dateCreation) = YEAR(CURRENT_DATE) " +
            "AND p IS NOT NULL " +
            "AND t.etudiant IS NOT NULL") // Nouvelle condition
    List<Tache> findOverdueTasks(@Param("today") LocalDate today,
                                 @Param("completedStates") List<EtatTache> completedStates);

    @Query("SELECT t FROM Tache t " +
            "JOIN t.sprint s " +
            "JOIN s.projet p " +
            "WHERE t.etudiant.id = :etudiantId " +
            "AND YEAR(t.dateCreation) = :year " +
            "AND p IS NOT NULL")
    List<Tache> findByEtudiantAndProjectAndYear(
            @Param("etudiantId") Long etudiantId,
            @Param("year") int year);
    @Query("SELECT t FROM Tache t " +
            "JOIN t.sprint s " +
            "JOIN s.projet p " +
            "WHERE t.etudiant.id = :etudiantId " +
            "AND YEAR(p.dateCreation) = :year " +
            "ORDER BY t.dateDebutEstimee")
    List<Tache> findByEtudiantAndCurrentYear(
            @Param("etudiantId") Long etudiantId,
            @Param("year") int year);
    List<Tache> findByEtudiantId(Long etudiantId);
    @Query("SELECT t FROM Tache t JOIN t.sprint s WHERE t.etudiant.id = :etudiantId AND s.id = :sprintId")
    List<Tache> findByEtudiantIdAndSprintId(@Param("etudiantId") Long etudiantId,
                                            @Param("sprintId") Long sprintId);
}
