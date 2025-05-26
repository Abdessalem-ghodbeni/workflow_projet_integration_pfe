package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IEtudiantRepository extends JpaRepository<Etudiant,Long> {
    Optional<Etudiant> findByEmail(String email);
    List<Etudiant> findBySpecialite(String specialite);
    @Query("SELECT COUNT(et) FROM Etudiant et WHERE et.equipe.tuteur.id = :tuteurId AND YEAR(et.equipe.dateCreation) = :year")
    int countEtudiantsByYear(Long tuteurId, Integer year);
    @Query("SELECT COUNT(et) FROM Etudiant et WHERE et.equipe.tuteur.id = :tuteurId")
    int countTotalEtudiants(Long tuteurId);




    // Pour l'année spécifique
    @Query("SELECT COUNT(et) FROM Etudiant et WHERE et.equipe.tuteur.id = :tuteurId AND YEAR(et.equipe.dateCreation) = :year")
    int countEtudiantsTuteurByYear(Long tuteurId, Integer year);

    @Query("SELECT COUNT(et) FROM Etudiant et WHERE YEAR(et.equipe.dateCreation) = :year")
    int countEtudiantsPlateformeByYear(Integer year);

    // Toutes années
    @Query("SELECT COUNT(et) FROM Etudiant et WHERE et.equipe.tuteur.id = :tuteurId")
    int countEtudiantsTuteurAllTime(Long tuteurId);

    @Query("SELECT COUNT(et) FROM Etudiant et")
    int countEtudiantsPlateformeAllTime();
}
