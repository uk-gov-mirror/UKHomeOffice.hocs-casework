package uk.gov.digital.ho.hocs.casework.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.digital.ho.hocs.casework.domain.model.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class StageDto {

    @JsonProperty("uuid")
    private UUID uuid;

    @JsonProperty("created")
    private LocalDateTime created;

    @JsonProperty("stageType")
    private String stageType;

    @JsonProperty("deadline")
    private LocalDate deadline;

    @JsonProperty("caseUUID")
    private UUID caseUUID;

    @JsonProperty("teamUUID")
    private UUID teamUUID;

    @JsonProperty("userUUID")
    private UUID userUUID;

    @JsonProperty("caseReference")
    private String caseReference;

    @JsonProperty("caseType")
    private String caseDataType;

    @JsonRawValue
    private String data;

    public static StageDto from(Stage stage) {

        String caseDataType = null;
        if (stage.getCaseDataType() != null) {
            caseDataType = stage.getCaseDataType();
        }
        return new StageDto(
                stage.getUuid(),
                stage.getCreated(),
                stage.getStageType(),
                stage.getDeadline(),
                stage.getCaseUUID(),
                stage.getTeamUUID(),
                stage.getUserUUID(),
                stage.getCaseReference(),
                caseDataType,
                stage.getData());
    }
}