package com.abdessalem.finetudeingenieurworkflow.DtoGithub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommitInfo {
    private String message;
    private Author author; // 👈 doit être présent
    private Author committer; // optionnel mais utile

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Author {
        private String name;
        private String email;
        private String date; // format ISO : "2025-04-29T22:37:36Z"
    }

}
