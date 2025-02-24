package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Etat;
import com.abdessalem.finetudeingenieurworkflow.Entites.Sujet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ISujetRepository extends JpaRepository<Sujet,Long> {
    Page<Sujet> findByTuteurId(Long tuteurId, Pageable pageable);
    Page<Sujet> findBySocieteId(Long SocieteId, Pageable pageable);
    List<Sujet> findByTitreContainingIgnoreCase(String titre);

    Page<Sujet> findByTitreContainingIgnoreCase(String titre, Pageable pageable);
    Page<Sujet> findBySocieteIsNotNull(Pageable pageable);
    Page<Sujet> findByTuteurIsNotNull(Pageable pageable);
    @Query("SELECT DISTINCT s.thematique FROM Sujet s WHERE s.societe IS NOT NULL")
    List<String> findDistinctThematiques();

    @Query("SELECT DISTINCT YEAR(s.dateModification) FROM Sujet s WHERE s.societe IS NOT NULL")
    List<Integer> findDistinctAnnees();

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
            "AND (:specialites IS NULL OR LOWER(s.specialite) IN :specialites) " +
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



    Page<Sujet> findByEtatOrderByDateModificationDesc(Etat etat, Pageable pageable);

    @Query("SELECT DISTINCT s.thematique FROM Sujet s WHERE s.etat = 'ACCEPTED'")
    List<String> findDistinctThematiquesAcceptedSujets();


    @Query("SELECT DISTINCT s.specialite FROM Sujet s WHERE s.etat = 'ACCEPTED'")
    List<String> findDistinctSpecialitesAcceptedSujets();

    @Query("SELECT DISTINCT YEAR(s.dateModification) FROM Sujet s WHERE s.etat = 'ACCEPTED'")
    List<Integer> findDistinctAnneesAcceptedSujets();

    @Query("SELECT DISTINCT s.titre FROM Sujet s WHERE s.etat = 'ACCEPTED' AND s.titre IS NOT NULL")
    List<String> findDistinctTitresAcceptedSujets();
}
