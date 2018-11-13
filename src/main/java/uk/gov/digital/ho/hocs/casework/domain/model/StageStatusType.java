package uk.gov.digital.ho.hocs.casework.domain.model;

import lombok.Getter;

public enum StageStatusType {
    CREATED("CREATED"),
    UPDATED("UPDATED"),
    REJECTED("REJECTED"),
    COMPLETE("COMPLETED");

    @Getter
    private String displayValue;

    StageStatusType(String value) {
        displayValue = value;
    }
}
