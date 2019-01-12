package uk.gov.digital.ho.hocs.casework.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.digital.ho.hocs.casework.domain.model.CaseSummary;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GetCaseSummaryResponse {

    @JsonProperty("caseDeadline")
    LocalDate caseDeadline;

    @JsonProperty("stageDeadlines")
    Map<String, String> stageDeadlines;

    @JsonProperty("additionalFields")
    Set<AdditionalFieldDto> additionalFields;

    @JsonProperty("primaryCorrespondent")
    GetCorrespondentResponse primaryCorrespondent;

    @JsonProperty("primaryTopic")
    GetTopicResponse primaryTopic;

    @JsonProperty("activeStages")
    Set<ActiveStageDto> activeStages;

    public static GetCaseSummaryResponse from(CaseSummary caseSummary) {
        GetCorrespondentResponse getCorrespondentResponse = null;
        if (caseSummary.getPrimaryCorrespondent() != null) {
            getCorrespondentResponse = GetCorrespondentResponse.from(caseSummary.getPrimaryCorrespondent());
        }

        GetTopicResponse getTopicsResponse = null;
        if (caseSummary.getPrimaryTopic() != null) {
            getTopicsResponse = GetTopicResponse.from(caseSummary.getPrimaryTopic());
        }

        Set<ActiveStageDto> activeStageDtos = null;
        if (caseSummary.getActiveStages() != null) {
            activeStageDtos = caseSummary.getActiveStages().stream().map(ActiveStageDto::from).collect(Collectors.toSet());
        }

        Set<AdditionalFieldDto> additionalFieldDtos = null;
        if (caseSummary.getAdditionalFields() != null) {
            additionalFieldDtos = caseSummary.getAdditionalFields().stream().map(AdditionalFieldDto::from).collect(Collectors.toSet());
        }
        return new GetCaseSummaryResponse(caseSummary.getCaseDeadline(), caseSummary.getStageDeadlines(), additionalFieldDtos, getCorrespondentResponse, getTopicsResponse, activeStageDtos);
    }
}
