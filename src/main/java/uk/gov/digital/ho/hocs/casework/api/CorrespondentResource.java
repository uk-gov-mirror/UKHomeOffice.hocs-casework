package uk.gov.digital.ho.hocs.casework.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.hocs.casework.api.dto.GetCorrespondentResponse;
import uk.gov.digital.ho.hocs.casework.api.dto.CreateCorrespondentRequest;
import uk.gov.digital.ho.hocs.casework.api.dto.GetCorrespondentsResponse;
import uk.gov.digital.ho.hocs.casework.domain.model.Address;
import uk.gov.digital.ho.hocs.casework.domain.model.Correspondent;
import uk.gov.digital.ho.hocs.casework.security.AccessLevel;
import uk.gov.digital.ho.hocs.casework.security.Allocated;
import uk.gov.digital.ho.hocs.casework.security.AllocationLevel;
import uk.gov.digital.ho.hocs.casework.security.Authorised;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
public class CorrespondentResource {

    private final CorrespondentService correspondentService;

    @Autowired
    public CorrespondentResource(CorrespondentService correspondentService) {
        this.correspondentService = correspondentService;
    }

    @Allocated(allocatedTo = AllocationLevel.USER)
    @PostMapping(value = "/case/{caseUUID}/stage/{stageUUID}/correspondent")
    ResponseEntity addCorrespondentToCase(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID, @Valid @RequestBody CreateCorrespondentRequest request) {
        Address address = new Address(request.getPostcode(), request.getAddress1(), request.getAddress2(), request.getAddress3(), request.getCountry());
        correspondentService.createCorrespondent(caseUUID, request.getType(), request.getFullname(), address, request.getTelephone(), request.getEmail(), request.getReference());
        return ResponseEntity.ok().build();
    }

    @Authorised(accessLevel = AccessLevel.READ)
    @GetMapping(value = "/case/{caseUUID}/correspondent")
    ResponseEntity<GetCorrespondentsResponse> getCorrespondents(@PathVariable UUID caseUUID) {
        Set<Correspondent> correspondents = correspondentService.getCorrespondents(caseUUID);
        return ResponseEntity.ok(GetCorrespondentsResponse.from(correspondents));
    }

    @Authorised(accessLevel = AccessLevel.READ)
    @GetMapping(value = "/case/{caseUUID}/correspondent/{correspondentUUID}")
    ResponseEntity<GetCorrespondentResponse> getCorrespondent(@PathVariable UUID caseUUID, @PathVariable UUID correspondentUUID) {
        Correspondent correspondent = correspondentService.getCorrespondent(caseUUID, correspondentUUID);
        return ResponseEntity.ok(GetCorrespondentResponse.from(correspondent));
    }

    @Allocated(allocatedTo = AllocationLevel.USER)
    @DeleteMapping(value = "/case/{caseUUID}/stage/{stageUUID}/correspondent/{correspondentUUID}")
    ResponseEntity<GetCorrespondentResponse> deleteCorrespondent(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID ,@PathVariable UUID correspondentUUID) {
        correspondentService.deleteCorrespondent(caseUUID, correspondentUUID);
        return ResponseEntity.ok().build();
    }

}