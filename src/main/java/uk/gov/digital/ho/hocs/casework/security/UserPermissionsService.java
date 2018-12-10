package uk.gov.digital.ho.hocs.casework.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.casework.application.RequestData;
import uk.gov.digital.ho.hocs.casework.domain.model.CaseDataType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.casework.application.LogEvent.*;

@Service
@Slf4j
public class UserPermissionsService {

    private RequestData requestData;

    @Autowired
    public UserPermissionsService(RequestData requestData) {
        this.requestData = requestData;
    }

    public UUID getUserId() {
        return UUID.fromString(requestData.userId());
    }

    private static void getAccessLevel(Map<String, Map<String, Map<String, Set<AccessLevel>>>> permissions, String[] permission) {
        try {
            String unit = Optional.ofNullable(permission[1]).orElseThrow(() -> new SecurityExceptions.PermissionCheckException("Null unit Found", SECURITY_PARSE_ERROR));
            String team = Optional.ofNullable(permission[2]).orElseThrow(() -> new SecurityExceptions.PermissionCheckException("Null team Found", SECURITY_PARSE_ERROR));
            String caseType = Optional.ofNullable(permission[3]).orElseThrow(() -> new SecurityExceptions.PermissionCheckException("Invalid case type Found",SECURITY_PARSE_ERROR));
            String accessLevel = Optional.ofNullable(permission[4]).orElseThrow(() -> new SecurityExceptions.PermissionCheckException("Invalid access type Found",SECURITY_PARSE_ERROR));

            permissions.computeIfAbsent(unit, map -> new HashMap<>())
                    .computeIfAbsent(team, map -> new HashMap<>())
                    .computeIfAbsent(caseType, map -> new HashSet<>())
                    .add(AccessLevel.valueOf(accessLevel));

        } catch (SecurityExceptions.PermissionCheckException e) {
            log.error(e.getMessage(),value(EVENT, SECURITY_PARSE_ERROR));
        }
    }

    public AccessLevel getMaxAccessLevel(String caseType) {

        return getUserPermission()
                .flatMap(unit -> unit.getValue().values().stream())
                .flatMap(type -> type.getOrDefault(caseType, new HashSet<>()).stream())
                .max(Comparator.comparing(AccessLevel::getLevel))
                .orElseThrow(() ->
                        new SecurityExceptions.PermissionCheckException("User does not have any permissions for this case type", SECURITY_UNAUTHORISED));
    }

    public Set<AccessLevel> getUserAccessLevels(CaseDataType caseType) {

        return getUserPermission()
                .flatMap(unit -> unit.getValue().values().stream())
                .flatMap(type -> type.getOrDefault(caseType.getDisplayCode(), new HashSet<>()).stream())
                .collect(Collectors.toSet());
    }


    public Set<String> getUserUnits() {
        return getUserPermission()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<UUID> getUserTeams() {
        return getUserPermission()
                .flatMap(unit -> unit.getValue().entrySet().stream())
                .map(team -> UUID.fromString(team.getKey()))
                .collect(Collectors.toSet());
    }

    public Set<String> getUserCaseTypes() {
        return getUserPermission()
                .flatMap(unit -> unit.getValue().values().stream())
                .flatMap(team -> team.entrySet().stream())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    Stream<Map.Entry<String, Map<String, Map<String, Set<AccessLevel>>>>> getUserPermission() {
        Map<String, Map<String, Map<String, Set<AccessLevel>>>> permissions = new HashMap<>();

        Arrays.stream(requestData.groupsArray())
                .map(group -> group.split("/"))
                .filter(group -> group.length >= 5)
                .forEach(group -> getAccessLevel(permissions, group));

        return permissions.entrySet().stream();
    }

}
