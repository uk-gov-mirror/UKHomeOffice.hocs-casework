package uk.gov.digital.ho.hocs.casework.client.infoclient;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.casework.domain.model.StageType;

import java.time.LocalDate;
import java.util.Map;

@NoArgsConstructor
@Getter
public class InfoGetDeadlinesResponse {

    @JsonProperty("deadlines")
    private Map<StageType, LocalDate> deadlines;
}