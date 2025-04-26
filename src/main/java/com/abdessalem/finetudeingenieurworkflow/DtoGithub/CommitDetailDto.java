package com.abdessalem.finetudeingenieurworkflow.DtoGithub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommitDetailDto {
    private CommitInfo commit;
    private Stats stats;
}
