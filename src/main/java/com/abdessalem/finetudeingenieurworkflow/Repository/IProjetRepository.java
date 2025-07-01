package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Equipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.Projet;
import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IProjetRepository extends JpaRepository<Projet,Long> {
    boolean existsByEquipeAndSujet(Equipe equipe, Sujet sujet);
    Optional<Projet> findByEquipeAndSujet(Equipe equipe, Sujet sujet);

    @Query("SELECT p FROM Projet p WHERE p.equipe.id = :equipeId")
    Optional<Projet> findByEquipeId(@Param("equipeId") Long equipeId);
///// projet statistique
@Query("SELECT COUNT(p) FROM Projet p WHERE p.equipe.tuteur = :tutor AND YEAR(p.dateCreation) = :year")
int countCompletedByTuteur(@Param("tutor") Tuteur tutor, @Param("year") int year);

    @Query("SELECT MONTH(p.dateCreation), COUNT(p) " +
            "FROM Projet p " +
            "WHERE p.equipe.tuteur.id = :tutorId " +
            "GROUP BY MONTH(p.dateCreation)")
    List<Object[]> countProjectsByMonth(@Param("tutorId") Long tutorId);
}
