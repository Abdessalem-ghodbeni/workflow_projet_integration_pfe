package com.abdessalem.finetudeingenieurworkflow.Repository;

import com.abdessalem.finetudeingenieurworkflow.Entites.CodeAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeAnalysisResultRepository extends JpaRepository<CodeAnalysisResult,Long> {
    List<CodeAnalysisResult> findByTacheIdOrderByDateDerniereAnalyseGitDesc(Long tacheId);
}
