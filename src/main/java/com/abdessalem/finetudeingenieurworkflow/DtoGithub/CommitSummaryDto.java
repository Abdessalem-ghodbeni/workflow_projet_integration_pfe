package com.abdessalem.finetudeingenieurworkflow.DtoGithub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommitSummaryDto {
    private String sha;
    private String url;

    private CommitInfo commit;

}
