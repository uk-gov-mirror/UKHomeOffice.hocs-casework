package uk.gov.digital.ho.hocs.casework.client.infoclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.digital.ho.hocs.casework.application.RestHelper;
import uk.gov.digital.ho.hocs.casework.domain.model.CaseDataType;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class InfoClient {

    private final RestHelper restHelper;
    private final String serviceBaseURL;


    @Autowired
    public InfoClient(RestHelper restHelper,
                      @Value("${hocs.info-service}") String infoService) {
        this.restHelper = restHelper;
        this.serviceBaseURL = infoService;
    }

    public Set<CaseDataType> getCaseTypes() {
        ResponseEntity<GetCaseTypesResponse> response = restHelper.get(serviceBaseURL, "/caseType", GetCaseTypesResponse.class);
        return response.getBody().getCaseTypes();
    }


    @Cacheable(value = "getCaseTypeByShortCode")
    public CaseDataType getCaseTypeByShortCode(String shortCode) {
        ResponseEntity<CaseDataType> response = restHelper.get(serviceBaseURL, String.format("/caseType/shortCode/%s", shortCode), CaseDataType.class);
        return response.getBody();
    }

    public InfoTopic getTopic(UUID topicUUID) {
        ResponseEntity<InfoTopic> response = restHelper.get(serviceBaseURL, String.format("/topic/%s", topicUUID), InfoTopic.class);
        return response.getBody();
    }


    //TODO: call info service endpoint to get case summary field filter once end point is complete
    public Set<String> getCaseSummaryFields(String shortCode) {
       return new HashSet<String>(){{
           add("TEMPCReference");
           add("CopyNumberTen");
       }};
        // ResponseEntity<Set<String>> response = restHelper.get(serviceBaseURL, String.format("/caseType/shortCode/%s/summary", shortCode), Set<String>.class);
    }

    public Map<String, LocalDate> getDeadlines(String caseType, LocalDate localDate) {
        ResponseEntity<InfoGetDeadlinesResponse> response = restHelper.get(serviceBaseURL, String.format("/casetype/%s/deadlines/%s", caseType, localDate), InfoGetDeadlinesResponse.class);
        Map<String, LocalDate> deadlines = response.getBody().getDeadlines();
        return deadlines;
    }

    public Set<InfoNominatedPeople> getNominatedPeople(UUID teamUUID) {
        ResponseEntity<InfoGetNominatedPeopleResponse> response = restHelper.get(serviceBaseURL, String.format("/nominatedpeople/%s", teamUUID), InfoGetNominatedPeopleResponse.class);
        return response.getBody().getNominatedPeople();
    }



}