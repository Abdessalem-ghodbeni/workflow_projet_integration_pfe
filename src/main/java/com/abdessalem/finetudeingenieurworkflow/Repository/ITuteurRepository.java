package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Tuteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ITuteurRepository extends JpaRepository<Tuteur,Long> {
    Page<Tuteur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);
    @Query("SELECT t FROM Tuteur t WHERE t.is_Chef_Options = true")
    List<Tuteur> findAllChefOptionsTuteurs();
//    long countByChefOptionsTrue();
@Query("SELECT COUNT(t) FROM Tuteur t WHERE t.is_Chef_Options = true")
long countChefOptions();

    @Query("SELECT COUNT(t) FROM Tuteur t WHERE t.is_Chef_departement = true")
    long countChefDepartement();


}
