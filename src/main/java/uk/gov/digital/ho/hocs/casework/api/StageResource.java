package uk.gov.digital.ho.hocs.casework.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.digital.ho.hocs.casework.api.dto.*;
import uk.gov.digital.ho.hocs.casework.application.RequestData;
import uk.gov.digital.ho.hocs.casework.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.casework.client.infoclient.UserDto;
import uk.gov.digital.ho.hocs.casework.domain.model.Stage;
import uk.gov.digital.ho.hocs.casework.security.AccessLevel;
import uk.gov.digital.ho.hocs.casework.security.Allocated;
import uk.gov.digital.ho.hocs.casework.security.AllocationLevel;
import uk.gov.digital.ho.hocs.casework.security.Authorised;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
class StageResource {

    private final StageService stageService;
    private final InfoClient infoClient;

    @Autowired
    public StageResource(StageService stageService, InfoClient infoClient) {
        this.stageService = stageService;
        this.infoClient = infoClient;
    }

    @Authorised(accessLevel = AccessLevel.WRITE)
    @PostMapping(value = "/case/{caseUUID}/stage")
    ResponseEntity<CreateStageResponse> createStage(@PathVariable UUID caseUUID, @RequestBody CreateStageRequest request) {
        Stage stage = stageService.createStage(caseUUID, request.getType(), request.getTeamUUID(), request.getUserUUID(), request.getAllocationType(), request.getTransitionNoteUUID());
        return ResponseEntity.ok(CreateStageResponse.from(stage));
    }

    @Authorised(accessLevel = AccessLevel.READ)
    @PutMapping(value = "/case/{caseUUID}/stage/{stageUUID}/recreate")
    ResponseEntity recreateStageTeam(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID, @RequestBody RecreateStageRequest request) {
        stageService.recreateStage(caseUUID, request.getStageUUID(), request.getStageType());
        return ResponseEntity.ok().build();
    }

    @Authorised(accessLevel = AccessLevel.READ)
    @GetMapping(value = "/case/{caseUUID}/stage/{stageUUID}")
    ResponseEntity<GetStageResponse> getStage(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID) {
        Stage stage = stageService.getActiveStage(caseUUID, stageUUID);
        return ResponseEntity.ok(GetStageResponse.from(stage));
    }

    @Authorised(accessLevel = AccessLevel.READ)
    @PutMapping(value = "/case/{caseUUID}/stage/{stageUUID}/user")
    ResponseEntity updateStageUser(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID, @RequestBody UpdateStageUserRequest request) {
        stageService.updateStageUser(caseUUID, stageUUID, request.getUserUUID());
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/case/team/{teamUUID}/allocate/user/next")
    ResponseEntity allocateStageUser(@PathVariable UUID teamUUID, @RequestHeader(RequestData.USER_ID_HEADER) UUID userUUID) {
        Stage stage = stageService.getUnassignedAndActiveStageByTeamUUID(teamUUID, userUUID);
        if (stage == null) {
            return ResponseEntity.ok(stage);
        }
        return ResponseEntity.ok(GetStageResponse.from(stage));
    }

    @GetMapping(value = "/case/{caseUUID}/stage/{stageUUID}/user")
    ResponseEntity<UUID> getStageUser(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID) {
        UUID userUUID = stageService.getStageUser(caseUUID, stageUUID);
        return ResponseEntity.ok(userUUID);
    }

    @Authorised(accessLevel = AccessLevel.READ)
    @PutMapping(value = "/case/{caseUUID}/stage/{stageUUID}/team")
    ResponseEntity updateStageTeam(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID, @RequestBody UpdateStageTeamRequest request) {
        stageService.updateStageTeam(caseUUID, stageUUID, request.getTeamUUID(), request.getAllocationType());
        return ResponseEntity.ok().build();
    }

    @Authorised(accessLevel = AccessLevel.READ)
    @PutMapping(value = "/case/{caseUUID}/stage/{stageUUID}/transitionNote")
    ResponseEntity updateStageTransitionNote(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID, @RequestBody UpdateStageTeamRequest request) {
        stageService.updateStageTeam(caseUUID, stageUUID, request.getTeamUUID(), request.getAllocationType());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/case/{caseUUID}/stage/{stageUUID}/team")
    ResponseEntity<UUID> getStageTeam(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID) {
        UUID teamUUID = stageService.getStageTeam(caseUUID, stageUUID);
        return ResponseEntity.ok(teamUUID);
    }

    @GetMapping(value = "/case/{caseUUID}/stage/{stageUUID}/team/members")
    public ResponseEntity<List<UserDto>> getUsersForTeamByStage(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID) {
        UUID teamUUID = stageService.getStageTeam(caseUUID, stageUUID);
        List<UserDto> users;
        if (teamUUID != null) {
            users = infoClient.getUsersForTeam(teamUUID);
        } else {
            users = infoClient.getUsersForTeamByStage(caseUUID, stageUUID);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/case/{caseUUID}/stage/{stageUUID}/type")
    ResponseEntity<String> getStageTypeFromStageData(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID) {
        String stageType = stageService.getStageTypeFromStageData(caseUUID, stageUUID);
        return ResponseEntity.ok(stageType);
    }

    @GetMapping(value = "/stage/team/{teamUUID}")
    ResponseEntity<GetStagesResponse> getActiveStagesByTeamUUID(@PathVariable UUID teamUUID) {
        Set<Stage> activeStages = stageService.getActiveStagesByTeamUUID(teamUUID);
        return ResponseEntity.ok(GetStagesResponse.from(activeStages));
    }

    @GetMapping(value = "/stage")
    ResponseEntity<GetStagesResponse> getActiveStages() {
        Set<Stage> activeStages = stageService.getActiveStagesForUser();
        return ResponseEntity.ok(GetStagesResponse.from(activeStages));
    }

    @GetMapping(value = "/case/{reference:[a-zA-Z]{2,}%2F[0-9]{7}%2F[0-9]{2}}/stage")
    ResponseEntity<GetStagesResponse> getActiveStagesForCase(@PathVariable String reference) throws UnsupportedEncodingException {
        String decodedRef = URLDecoder.decode(reference, StandardCharsets.UTF_8.name());
        Set<Stage> activeStages = stageService.getActiveStagesByCaseReference(decodedRef);
        return ResponseEntity.ok(GetStagesResponse.from(activeStages));
    }

    @GetMapping(value = "/stage/team/{teamUUID}/user/{userUUID}")
    ResponseEntity<Set<UUID>> getActiveStageCaseUUIDsForUserAndTeam(@PathVariable UUID userUUID, @PathVariable UUID teamUUID) {
        Set<UUID> caseUUIDs = stageService.getActiveStageCaseUUIDsForUserAndTeam(userUUID, teamUUID);
        return ResponseEntity.ok(caseUUIDs);
    }

    @PostMapping(value = "/search")
    ResponseEntity<GetStagesResponse> search(@Valid @RequestBody SearchRequest request) {
        Set<Stage> stages = stageService.search(request);
        return ResponseEntity.ok(GetStagesResponse.from(stages));
    }

    @GetMapping(value = "/stage/case/{caseUUID}")
    ResponseEntity<GetStagesResponse> getAllStagesByCase(@PathVariable UUID caseUUID) {
        Set<Stage> stages = stageService.getAllStagesForCaseByCaseUUID(caseUUID);
        return ResponseEntity.ok(GetStagesResponse.from(stages));
    }

    @Allocated(allocatedTo = AllocationLevel.USER_OR_TEAM)
    @PostMapping(value = "/case/{caseUUID}/stage/{stageUUID}/withdraw")
    ResponseEntity withdrawCase(@PathVariable UUID caseUUID, @PathVariable UUID stageUUID, @RequestBody WithdrawCaseRequest request) {
        stageService.withdrawCase(caseUUID, stageUUID, request);
        return ResponseEntity.ok("Case withdrawn successfully");
    }
}