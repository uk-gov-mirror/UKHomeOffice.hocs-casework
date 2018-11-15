package uk.gov.digital.ho.hocs.casework.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.digital.ho.hocs.casework.api.dto.GetCaseNotesResponse;
import uk.gov.digital.ho.hocs.casework.domain.model.CaseNote;
import uk.gov.digital.ho.hocs.casework.security.AccessLevel;
import uk.gov.digital.ho.hocs.casework.security.Authorised;

import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
public class CaseNoteResource {

    private final CaseNoteService caseNoteService;

    @Autowired
    public CaseNoteResource(CaseNoteService caseNoteService) {
        this.caseNoteService = caseNoteService;
    }

    @Authorised(accessLevel = AccessLevel.SUMMARY)
    @GetMapping(value = "/case/{caseUUID}/note", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GetCaseNotesResponse> getCaseNotesForCase(@PathVariable UUID caseUUID) {
        Set<CaseNote> caseNoteData = caseNoteService.getCaseNotes(caseUUID);
        return ResponseEntity.ok(GetCaseNotesResponse.from(caseNoteData));
    }
}
