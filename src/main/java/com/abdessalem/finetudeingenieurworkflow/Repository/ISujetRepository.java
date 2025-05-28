package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Etat;
import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ISujetRepository extends JpaRepository<Sujet,Long> {
    Page<Sujet> findByTuteurId(Long tuteurId, Pageable pageable);
//    Sujet findByTitre(String titre);
    Page<Sujet> findBySocieteId(Long SocieteId, Pageable pageable);
//    List<Sujet> findByTitreContainingIgnoreCase(String titre);
  Sujet findByTitreContainingIgnoreCase(String titre);
    Page<Sujet> findByTitreContainingIgnoreCase(String titre, Pageable pageable);
    Page<Sujet> findBySocieteIsNotNull(Pageable pageable);
    Page<Sujet> findByTuteurIsNotNull(Pageable pageable);
    @Query("SELECT DISTINCT s.thematique FROM Sujet s WHERE s.societe IS NOT NULL")
    List<String> findDistinctThematiques();

    @Query("SELECT DISTINCT YEAR(s.dateModification) FROM Sujet s WHERE s.societe IS NOT NULL")
    List<Integer> findDistinctAnnees();
  Page<Sujet> findByTuteurIsNotNullAndSpecialite(String specialite, Pageable pageable);

    @Query("SELECT DISTINCT s.societe.nom FROM Sujet s WHERE s.societe IS NOT NULL")
    List<String> findDistinctSocietes();

    @Query("SELECT DISTINCT s.specialite FROM Sujet s WHERE s.societe IS NOT NULL")
    List<String> findDistinctSpecialites();

    @Query("SELECT DISTINCT s.etat FROM Sujet s WHERE s.societe IS NOT NULL")
    List<Etat> findDistinctEtats();

    @Query("SELECT s FROM Sujet s WHERE s.societe IS NOT NULL " +
            "AND (:thematiques IS NULL OR s.thematique IN :thematiques) " +
            "AND (:annees IS NULL OR YEAR(s.dateModification) IN :annees) " +

            "AND (:societes IS NULL OR s.societe.nom IN :societes) " +
            "AND (:specialites IS NULL OR LOWER(s.specialite) IN :specialites)"+
            "AND (:etats IS NULL OR s.etat IN :etats)")
    Page<Sujet> findByFilters(
            @Param("thematiques") List<String> thematiques,
            @Param("annees") List<Integer> annees,
            @Param("societes") List<String> societes,
            @Param("specialites") List<String> specialites,
            @Param("etats") List<Etat> etats,
            Pageable pageable
    );

    //////tuteur
    @Query("SELECT DISTINCT s.thematique FROM Sujet s WHERE s.tuteur IS NOT NULL AND s.specialite = :specialite")
    List<String> findDistinctThematiquesSujetCreatedByTuteurBySpecialite(@Param("specialite") String specialite);

  @Query("SELECT DISTINCT YEAR(s.dateModification) FROM Sujet s WHERE s.tuteur IS NOT NULL AND s.specialite = :specialite")
  List<Integer> findDistinctAnneesSujetCreatedByTuteurBySpecialite(@Param("specialite") String specialite);

  @Query("SELECT DISTINCT s.titre FROM Sujet s WHERE s.tuteur IS NOT NULL AND s.specialite = :specialite")
  List<String> findDistinctTitresSujetCreatedByTuteurBySpecialite(@Param("specialite") String specialite);

  @Query("SELECT DISTINCT s.tuteur.nom FROM Sujet s WHERE s.tuteur IS NOT NULL AND s.specialite = :specialite")
  List<String> findDistinctTuteursNameBySpecialite(@Param("specialite") String specialite);

  @Query("SELECT DISTINCT s.etat FROM Sujet s WHERE s.tuteur IS NOT NULL AND s.specialite = :specialite")
  List<Etat> findDistinctEtatsSujetCreatedByTuteurBySpecialite(@Param("specialite") String specialite);
    // Récupeeeeeeerer les theeematiques distinctes ah la min gali wechbik
    @Query("SELECT DISTINCT s.thematique FROM Sujet s WHERE s.tuteur IS NOT NULL")
    List<String> findDistinctThematiquesSujetCreatedByTuteur();


    @Query("SELECT DISTINCT YEAR(s.dateModification) FROM Sujet s WHERE s.tuteur IS NOT NULL")
    List<Integer> findDistinctAnneesSujetCreatedByTuteur();


    @Query("SELECT DISTINCT s.titre FROM Sujet s WHERE s.tuteur IS NOT NULL")
    List<String> findDistinctTitresSujetCreatedByTuteur();

    // Récupérer les noms distincts des tuteurs
    @Query("SELECT DISTINCT s.tuteur.nom FROM Sujet s WHERE s.tuteur IS NOT NULL")
    List<String> findDistinctTuteursName();


    @Query("SELECT DISTINCT s.specialite FROM Sujet s WHERE s.tuteur IS NOT NULL")
    List<String> findDistinctSpecialitesSujetCreatedByTuteur();

    @Query("SELECT DISTINCT s.etat FROM Sujet s WHERE s.tuteur IS NOT NULL")
    List<Etat> findDistinctEtatsSujetCreatedByTuteur();


    @Query("SELECT s FROM Sujet s WHERE s.tuteur IS NOT NULL " +
            "AND (:thematiques IS NULL OR s.thematique IN :thematiques) " +
            "AND (:annees IS NULL OR YEAR(s.dateModification) IN :annees) " +
            "AND (:titres IS NULL OR s.titre IN :titres) " +
            "AND (:tuteurs IS NULL OR s.tuteur.nom IN :tuteurs) " +
            "AND (:specialites IS NULL OR LOWER(s.specialite) IN :specialites)"+
            "AND (:etats IS NULL OR s.etat IN :etats)")
    Page<Sujet> findByFiltersTuteurs(
            @Param("thematiques") List<String> thematiques,
            @Param("annees") List<Integer> annees,
            @Param("titres") List<String> titres,
            @Param("tuteurs") List<String> tuteurs,
            @Param("specialites") List<String> specialites,
            @Param("etats") List<Etat> etats,
            Pageable pageable
    );

  @Query("SELECT s FROM Sujet s WHERE s.tuteur IS NOT NULL " +
          "AND LOWER(s.specialite) = :specialite " +  // Spécialité donnée
          "AND (:thematiques IS NULL OR s.thematique IN :thematiques) " +
          "AND (:annees IS NULL OR YEAR(s.dateModification) IN :annees) " +
          "AND (:titres IS NULL OR s.titre IN :titres) " +
          "AND (:tuteurs IS NULL OR s.tuteur.nom IN :tuteurs) " +
          "AND (:etats IS NULL OR s.etat IN :etats)")
  Page<Sujet> findByFiltersTuteursForSpecialite(
          @Param("specialite") String specialite,
          @Param("thematiques") List<String> thematiques,
          @Param("annees") List<Integer> annees,
          @Param("titres") List<String> titres,
          @Param("tuteurs") List<String> tuteurs,
          @Param("etats") List<Etat> etats,
          Pageable pageable
  );

    Page<Sujet> findByEtatOrderByDateModificationDesc(Etat etat, Pageable pageable);

    @Query("SELECT DISTINCT s.thematique FROM Sujet s WHERE s.etat = 'ACCEPTED'")
    List<String> findDistinctThematiquesAcceptedSujets();


    @Query("SELECT DISTINCT s.specialite FROM Sujet s WHERE s.etat = 'ACCEPTED'")
    List<String> findDistinctSpecialitesAcceptedSujets();

    @Query("SELECT DISTINCT YEAR(s.dateModification) FROM Sujet s WHERE s.etat = 'ACCEPTED'")
    List<Integer> findDistinctAnneesAcceptedSujets();

    @Query("SELECT DISTINCT s.titre FROM Sujet s WHERE s.etat = 'ACCEPTED' AND s.titre IS NOT NULL")
    List<String> findDistinctTitresAcceptedSujets();



    @Query("SELECT s FROM Sujet s " +
            "WHERE s.etat = 'ACCEPTED' " +
            "AND (:thematiques IS NULL OR s.thematique IN :thematiques) " +
            "AND (:specialites IS NULL OR LOWER(s.specialite) IN :specialites)"+
            "AND (:annees IS NULL OR YEAR(s.dateModification) IN :annees) " +
            "AND (:titres IS NULL OR s.titre IN :titres)")
    Page<Sujet> findFilteredAcceptedSujets(
            @Param("thematiques") List<String> thematiques,
            @Param("specialites") List<String> specialites,
            @Param("annees") List<Integer> annees,
            @Param("titres") List<String> titres,
            Pageable pageable
    );
    Page<Sujet> findByVisibleAuxEtudiantsTrueAndSpecialiteAndDateCreationBetween(
            String specialite,
            LocalDateTime startOfYear,
            LocalDateTime endOfYear,
            Pageable pageable
    );

    @Query("SELECT DISTINCT s.titre FROM Sujet s WHERE s.visibleAuxEtudiants = true AND s.specialite = :specialite")
    List<String> findDistinctTitresBySpecialite(@Param("specialite") String specialite);

    @Query("SELECT DISTINCT s.thematique FROM Sujet s WHERE s.visibleAuxEtudiants = true AND s.specialite = :specialite")
    List<String> findDistinctThematiquesBySpecialite(@Param("specialite") String specialite);



    @Query("SELECT s FROM Sujet s " +
            "WHERE s.visibleAuxEtudiants = true " +
            "AND LOWER(s.specialite) = LOWER(:specialite) " +
            "AND (:titres IS NULL OR s.titre IN :titres) " +
            "AND (:thematiques IS NULL OR s.thematique IN :thematiques)")
    Page<Sujet> findFilteredVisibleSujets(
            @Param("specialite") String specialite,
            @Param("titres") List<String> titres,
            @Param("thematiques") List<String> thematiques,
            Pageable pageable
    );



    Page<Sujet> findByVisibleAuxEtudiantsTrueAndSpecialiteIgnoreCaseAndTitreContainingIgnoreCase(
            String specialite,
            String titre,
            Pageable pageable
    );

List<Sujet>findByVisibleAuxEtudiantsTrueAndSpecialiteAndDateCreationBetween(
        String specialite,
        LocalDateTime startOfYear,
        LocalDateTime endOfYear);
  Optional<Sujet> findByTitre(String title);
  @Query("SELECT COUNT(s) FROM Sujet s WHERE s.tuteur.id = :tuteurId")
  int countTotalSujets(Long tuteurId);

  // Pour les sujets validés (année spécifique ou courante)
  @Query("SELECT COUNT(s) FROM Sujet s WHERE s.tuteur.id = :tuteurId AND s.etat = 'ACCEPTED' AND YEAR(s.dateCreation) = :year")
  int countValidatedSujetsByYear(Long tuteurId, Integer year);

  // Pour les sujets refusés
  @Query("SELECT COUNT(s) FROM Sujet s WHERE s.tuteur.id = :tuteurId AND s.etat = 'REFUSER' AND YEAR(s.dateCreation) = :year")
  int countRejectedSujetsByYear(Long tuteurId, Integer year);

  @Query("SELECT COUNT(s) FROM Sujet s WHERE YEAR(s.dateCreation) = :year")
  int countTotalSujetsByYear(@Param("year") Integer year);
  @Query("SELECT COUNT(s) FROM Sujet s")
  int countTotalSujetsAllTime();







  // SujetRepository.java
// Pour l'année sélectionnée
  @Query("SELECT COUNT(s) FROM Sujet s WHERE s.tuteur.id = :tuteurId AND YEAR(s.dateCreation) = :year")
  int countSujetsTuteurByYear(Long tuteurId, Integer year);

  @Query("SELECT COUNT(s) FROM Sujet s WHERE YEAR(s.dateCreation) = :year")
  int countSujetsPlateformeByYear(Integer year);

  // Toutes années
  @Query("SELECT COUNT(s) FROM Sujet s WHERE s.tuteur.id = :tuteurId")
  int countSujetsTuteurAllTime(Long tuteurId);

  @Query("SELECT COUNT(s) FROM Sujet s")
  int countSujetsPlateformeAllTime();



  //evolution te3 subject and tuteur
  @Query("SELECT YEAR(s.dateCreation) as annee, COUNT(s) as nombreSujets " +
          "FROM Sujet s " +
          "WHERE s.tuteur.id = :tuteurId " +
          "GROUP BY YEAR(s.dateCreation) " +
          "ORDER BY YEAR(s.dateCreation) DESC")
  List<Object[]> findSujetEvolutionByTuteur(@Param("tuteurId") Long tuteurId);




//statitique requetes
long countByEtat(Etat etat);

//  @Query("SELECT s.etat AS status, COUNT(s) AS count FROM Sujet s GROUP BY s.etat")
//  Map<String, Long> countSubjectsByStatus();
@Query("SELECT s.etat, COUNT(s) FROM Sujet s GROUP BY s.etat")
List<Object[]> countSubjectsByStatusGrouped();
  @Query("SELECT COUNT(s) FROM Sujet s WHERE s.tuteur = :tutor AND YEAR(s.dateCreation) = :year")
  int countByTuteurAndYear(@Param("tutor") Tuteur tutor, @Param("year") int year);









}
