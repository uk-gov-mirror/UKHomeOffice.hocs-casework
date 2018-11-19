package uk.gov.digital.ho.hocs.casework.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.digital.ho.hocs.casework.application.RequestData;
import uk.gov.digital.ho.hocs.casework.domain.model.CaseDataType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserPermissionsServiceTest {

    @Mock
    private RequestData requestData;


    private UserPermissionsService service;

    @Before
    public void setup() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }


    @Test
    public void shouldParseValidUserGroups() {
        String groups =
                "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/READ," +
                        "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/WRITE," +
                        "/DCU/f1825c7d-baff-4c09-8056-2166760ccbd2/MIN/WRITE," +
                        "/ABC/1c1e2f17-d5d9-4ff6-a023-6c40d76e1e9d/MIN/WRITE," +
                        "/ABC/1325fe16-b864-42c7-85c2-7cab2863fe01/MIN/OWNER";

        when(requestData.groups()).thenReturn(groups);
        service = new UserPermissionsService(requestData);
        Map<String, Map<String, Map<CaseDataType,Set<AccessLevel>>>> permissions = service.getUserPermission();
        assertThat(permissions.size()).isEqualTo(2);
    }


    @Test
    public void shouldIgnoreInvalidUserGroups() {
        String groups =
                "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/READ," +
                        "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/," +
                        "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01," +
                        "/ABC/," +
                        "/ABC/1325fe16-b864-42c7-85c2-7cab2863fe01/MIN/OWNER";

        when(requestData.groups()).thenReturn(groups);
        service = new UserPermissionsService(requestData);
        Map<String, Map<String, Map<CaseDataType,Set<AccessLevel>>>> permissions = service.getUserPermission();
        assertThat(permissions.size()).isEqualTo(2);
    }



    @Test
    public void shouldGetPermissionsForCaseType() {
        String groups =
                "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/READ," +
                        "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/WRITE," +
                        "/DCU/f1825c7d-baff-4c09-8056-2166760ccbd2/MIN/WRITE," +
                        "/ABC/1c1e2f17-d5d9-4ff6-a023-6c40d76e1e9d/MIN/WRITE," +
                        "/ABC/1325fe16-b864-42c7-85c2-7cab2863fe01/MIN/OWNER";

        when(requestData.groups()).thenReturn(groups);
        service = new UserPermissionsService(requestData);
        Set<AccessLevel> userAccessLevels = service.getUserAccessLevels(CaseDataType.MIN);
        assertThat(userAccessLevels.size()).isEqualTo(2);
        assertThat(userAccessLevels).contains(AccessLevel.WRITE);
        assertThat(userAccessLevels).contains(AccessLevel.OWNER);

    }

    @Test
    public void shouldGetUnitsForUser() {
        String groups =
                "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/READ," +
                        "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/WRITE," +
                        "/DCU/f1825c7d-baff-4c09-8056-2166760ccbd2/MIN/WRITE," +
                        "/ABC/1c1e2f17-d5d9-4ff6-a023-6c40d76e1e9d/MIN/WRITE," +
                        "/ABC/1325fe16-b864-42c7-85c2-7cab2863fe01/MIN/OWNER";

        when(requestData.groups()).thenReturn(groups);
        service = new UserPermissionsService(requestData);
        Set<String> units = service.getUserUnits();
        assertThat(units).contains("DCU");
        assertThat(units).contains("ABC");

    }

    @Test
    public void shouldGetTeamsForUser() {
        String groups =
                "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/READ," +
                        "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/WRITE," +
                        "/DCU/f1825c7d-baff-4c09-8056-2166760ccbd2/MIN/WRITE," +
                        "/ABC/1c1e2f17-d5d9-4ff6-a023-6c40d76e1e9d/MIN/WRITE," +
                        "/ABC/1325fe16-b864-42c7-85c2-7cab2863fe01/MIN/OWNER";

        when(requestData.groups()).thenReturn(groups);
        service = new UserPermissionsService(requestData);
        Set<UUID> teams = service.getUserTeams();
        assertThat(teams).contains(UUID.fromString("1c1e2f17-d5d9-4ff6-a023-6c40d76e1e9d"));
        assertThat(teams).contains(UUID.fromString("f1825c7d-baff-4c09-8056-2166760ccbd2"));
        assertThat(teams).contains(UUID.fromString("1325fe16-b864-42c7-85c2-7cab2863fe01"));
    }

    @Test
    public void shouldGetCaseTypesForUser() {
        String groups =
                "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/READ," +
                        "/DCU/1325fe16-b864-42c7-85c2-7cab2863fe01/TRO/WRITE," +
                        "/DCU/f1825c7d-baff-4c09-8056-2166760ccbd2/MIN/WRITE," +
                        "/ABC/1c1e2f17-d5d9-4ff6-a023-6c40d76e1e9d/MIN/WRITE," +
                        "/ABC/1325fe16-b864-42c7-85c2-7cab2863fe01/MIN/OWNER";

        when(requestData.groups()).thenReturn(groups);
        service = new UserPermissionsService(requestData);
        Set<CaseDataType> caseTypes = service.getUserCaseTypes();
        assertThat(caseTypes).contains(CaseDataType.TRO);
        assertThat(caseTypes).contains(CaseDataType.MIN);
    }
}