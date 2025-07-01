package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Equipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.EtatEquipe;
import com.abdessalem.finetudeingenieurworkflow.Entites.Etudiant;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IEquipeRepository extends JpaRepository<Equipe,Long> {
    @Query("SELECT DISTINCT e FROM Equipe e JOIN e.etudiants et WHERE et.specialite = :specialite")
    List<Equipe> findEquipesByEtudiantSpecialite(String specialite);
    List<Equipe> findByTuteurId(Long tuteurId);


    Optional<Equipe> findByNom(String nom);

    @Query("select distinct e from Equipe e " +
            "join e.etudiants et " +
            "join et.formResponses fr " +
            "where fr.form.id = :formId")
    List<Equipe> findEquipesByFormId(@Param("formId") Long formId);

    @Query("SELECT DISTINCT e FROM Equipe e JOIN e.etudiants et " +
            "WHERE et.specialite = :specialite AND YEAR(e.dateCreation) = YEAR(CURRENT_DATE)")
    List<Equipe> findEquipesBySpecialiteAndCurrentYear(String specialite);

    @Query("SELECT DISTINCT e FROM Equipe e " +
            "JOIN e.etudiants et " +
            "WHERE (YEAR(e.dateCreation) = YEAR(CURRENT_DATE) " +
            "   OR YEAR(e.dateModification) = YEAR(CURRENT_DATE)) " +
            "AND et.specialite = :specialite")
    List<Equipe> findEquipesByYearAndSpecialite(String specialite);


    @Query("SELECT e FROM Etudiant e " +
            "WHERE e.specialite = :specialite " +
            "AND EXTRACT(YEAR FROM e.dateModification) = EXTRACT(YEAR FROM CURRENT_DATE)")
    List<Etudiant> findBySpecialiteAndCurrentYear(@Param("specialite") String specialite);


    @Query("SELECT COUNT(e) FROM Equipe e WHERE e.tuteur.id = :tuteurId AND YEAR(e.dateCreation) = :year")
    int countEquipesByYear(Long tuteurId, Integer year);
    @Query("SELECT COUNT(e) FROM Equipe e WHERE e.tuteur.id = :tuteurId")
    int countTotalEquipes(Long tuteurId);




    // Pour l'année spécifique
    @Query("SELECT COUNT(e) FROM Equipe e WHERE e.tuteur.id = :tuteurId AND YEAR(e.dateCreation) = :year")
    int countEquipesTuteurByYear(Long tuteurId, Integer year);

    @Query("SELECT COUNT(e) FROM Equipe e WHERE YEAR(e.dateCreation) = :year")
    int countEquipesPlateformeByYear(Integer year);

    // Toutes années
    @Query("SELECT COUNT(e) FROM Equipe e WHERE e.tuteur.id = :tuteurId")
    int countEquipesTuteurAllTime(Long tuteurId);

    @Query("SELECT COUNT(e) FROM Equipe e")
    int countEquipesPlateformeAllTime();

//requete statistique
long countByEtat(EtatEquipe etat);

    @Query("SELECT e.etat AS status, COUNT(e) AS count FROM Equipe e GROUP BY e.etat")
    Map<String, Long> countTeamsByStatus();

    @Query("SELECT COUNT(e) FROM Equipe e WHERE e.tuteur = :tutor AND YEAR(e.dateCreation) = :year")
    int countByTuteurAndYear(@Param("tutor") Tuteur tutor, @Param("year") int year);

    @Query("SELECT AVG(SIZE(e.etudiants)) FROM Equipe e WHERE e.tuteur = :tutor")
    Double averageTeamSize(@Param("tutor") Tuteur tutor);
    @Query("SELECT e.etat, COUNT(e) FROM Equipe e GROUP BY e.etat")
    List<Object[]> countTeamsByStatusGrouped();
    @Query("SELECT DISTINCT e FROM Equipe e " +
            "JOIN e.projets p " +
            "JOIN p.sujet s " +
            "WHERE s.societe.id = :societeId")
    List<Equipe> findEquipesBySocieteAuteur(@Param("societeId") Long societeId);
}
