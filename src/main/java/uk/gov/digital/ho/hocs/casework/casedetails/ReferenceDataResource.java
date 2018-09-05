package uk.gov.digital.ho.hocs.casework.casedetails;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.digital.ho.hocs.casework.casedetails.dto.GetReferenceResponse;
import uk.gov.digital.ho.hocs.casework.casedetails.model.ReferenceData;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
public class ReferenceDataResource {

    private final ReferenceDataService referenceDataService;

    @Autowired
    public ReferenceDataResource(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @GetMapping(value = "/case/{caseUUID}/reference", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GetReferenceResponse> getReference(@PathVariable UUID caseUUID) {
        ReferenceData referenceData = referenceDataService.getReferenceData(caseUUID);
        return ResponseEntity.ok(GetReferenceResponse.from(referenceData));
    }

}
