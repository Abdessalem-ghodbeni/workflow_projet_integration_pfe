package com.abdessalem.finetudeingenieurworkflow.Entites.DTOSsStatistique;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsDTO {
    private Integer selectedYear;
    private YearStats selectedYearStats;
    private AllTimeStats allTimeStats;
}
