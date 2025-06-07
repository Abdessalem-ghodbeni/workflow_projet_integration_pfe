package com.abdessalem.finetudeingenieurworkflow.Entites;

public enum TypeAppreciation {
    EXCELLENT("Travail de très haute qualité, dépasse les attentes."),
    TRES_BIEN("Travail solide, montre une bonne compréhension du sujet."),
    BIEN("Travail satisfaisant, répond aux critères établis."),
    SATISFAISANT("Efforts notables, mais des améliorations sont possibles."),
    ACCEPTABLE("Résultats corrects, mais nécessite davantage de travail."),
    INSUFFISANT("Travail en dessous des attentes, des efforts supplémentaires sont nécessaires."),
    A_REVOIR("Pas conforme aux exigences, nécessite une réévaluation complète.");

    private final String description;

    TypeAppreciation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
