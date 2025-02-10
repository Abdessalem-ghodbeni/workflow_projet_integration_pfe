package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.Societe;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ISocieteRepository extends JpaRepository<Societe,Long> {
    Page<Societe> findByNomContainingIgnoreCaseOrSecteurActiviteContainingIgnoreCase(String nom, String secteurActivite, Pageable pageable);

    @Query("SELECT s FROM Societe s WHERE " +
            "(:secteurs IS NULL OR s.secteurActivite IN :secteurs) AND " +
            "(:villes IS NULL OR s.ville IN :villes) AND " +
            "(:active IS NULL OR s.isActive = :active)")
    Page<Societe> findFilteredSocietes(
            @Param("secteurs") List<String> secteurs,
            @Param("villes") List<String> villes,
            @Param("active") Boolean active,
            Pageable pageable);


}
