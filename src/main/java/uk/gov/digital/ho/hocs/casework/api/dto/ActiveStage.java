package uk.gov.digital.ho.hocs.casework.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.digital.ho.hocs.casework.domain.model.Stage;
import uk.gov.digital.ho.hocs.casework.domain.model.StageType;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ActiveStage {

    private UUID stageUUID;
    private StageType stage;
    private UUID assignedToUserUUID;
    private UUID assignedToTeamUUID;

    public static ActiveStage from(Stage stage) {
        return new ActiveStage(stage.getUuid(), stage.getStageType(),stage.getUserUUID(), stage.getTeamUUID());

    }
}
