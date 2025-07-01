package com.abdessalem.finetudeingenieurworkflow.DtoGithub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
// Fichier: CommitDetailDto.java
@Data
public class CommitDetailDto {
    private CommitInfo commit;
    private Stats stats;
    private String sha; // Ajouter ce champ
    private CommitDetails details; // Ajouter cette section

    @Data
    public static class CommitDetails {
        private Author author;
        // Nouvelle méthode sécurisée
        public Optional<Author> getSafeAuthor() {
            return Optional.ofNullable(author);
        }
    }

    @Data
    public static class Author {
        private String date; // Format ISO
    }
}