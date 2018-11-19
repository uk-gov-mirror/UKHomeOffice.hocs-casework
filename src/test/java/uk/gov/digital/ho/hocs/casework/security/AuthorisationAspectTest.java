package uk.gov.digital.ho.hocs.casework.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.digital.ho.hocs.casework.api.CaseDataService;
import uk.gov.digital.ho.hocs.casework.api.dto.CreateCaseRequest;
import uk.gov.digital.ho.hocs.casework.domain.model.CaseData;
import uk.gov.digital.ho.hocs.casework.domain.model.CaseDataType;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthorisationAspectTest {

    @Mock
    private UserPermissionsService userService;

    @Mock
    private CaseDataService caseService;

    @Mock
    private Authorised annotation;

    private AuthorisationAspect aspect;

    private UUID caseUUID = UUID.randomUUID();

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Before
    public void setup() {
        when(userService.getMaxAccessLevel(any())).thenReturn(AccessLevel.OWNER);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        aspect = new AuthorisationAspect(caseService, userService);

    }

    @Test
    public void shouldCaseServiceWhenExistingCase() throws Throwable {

        Object[] args = new Object[1];
        args[0] = caseUUID;

        when(caseService.getCase(caseUUID)).thenReturn(new CaseData(CaseDataType.MIN, 123456789L));
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        when(annotation.accessLevel()).thenReturn(AccessLevel.READ);

        aspect.validateUserAccess(proceedingJoinPoint, annotation);

        verify(caseService, times(1)).getCase(caseUUID);
        verify(userService, times(1)).getMaxAccessLevel(CaseDataType.MIN);
        verify(proceedingJoinPoint, atLeast(1)).getArgs();
    }

    @Test
    public void shouldNotCallCaseServiceWhenNewCase() throws Throwable {

        Object[] args = new Object[1];
        args[0] = new CreateCaseRequest(CaseDataType.MIN, new HashMap<>());
        when(annotation.accessLevel()).thenReturn(AccessLevel.READ);
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        aspect.validateUserAccess(proceedingJoinPoint,annotation);

        verify(caseService, never()).getCase(caseUUID);
        verify(userService, times(1)).getMaxAccessLevel(CaseDataType.MIN);
        verify(proceedingJoinPoint, atLeast(1)).getArgs();
    }


    @Test
    public void shouldProceedIfUserHasPermission() throws Throwable {

        Object[] args = new Object[1];
        args[0] = caseUUID;
        when(caseService.getCase(caseUUID)).thenReturn(new CaseData(CaseDataType.MIN, 123456789L));
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        when(annotation.accessLevel()).thenReturn(AccessLevel.READ);

        aspect.validateUserAccess(proceedingJoinPoint,annotation);

        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test(expected = SecurityExceptions.PermissionCheckException.class)
    public void shouldNotProceedIfUserDoesNotHavePermission() throws Throwable {

        Object[] args = new Object[1];
        args[0] = caseUUID;
        when(userService.getMaxAccessLevel(any())).thenThrow(new SecurityExceptions.PermissionCheckException("User does not have any permission onf this case type"));
        when(caseService.getCase(caseUUID)).thenReturn(new CaseData(CaseDataType.MIN, 123456789L));
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        when(annotation.accessLevel()).thenReturn(AccessLevel.READ);

        aspect.validateUserAccess(proceedingJoinPoint,annotation);

        verify(proceedingJoinPoint, never()).proceed();
    }

    @Test(expected = SecurityExceptions.PermissionCheckException.class)
    public void shouldThrowExceptionOnError() throws Throwable {

        Object[] args = new Object[1];
        args[0] = "bad UUID";
        when(annotation.accessLevel()).thenReturn(AccessLevel.READ);
        when(proceedingJoinPoint.getArgs()).thenReturn(args);

        aspect.validateUserAccess(proceedingJoinPoint,annotation);

        verify(proceedingJoinPoint, never()).proceed();
    }


    @Test
    public void shouldGetRequestedPermissionTypeFromRequestWhenAnnotationIsNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThat(aspect.getAccessRequestAccessLevel()).isEqualTo(AccessLevel.SUMMARY);
    }

    @Test
    public void shouldGetRequestedPermissionTypeFromRequestWhenAnnotationIsSet() throws Throwable {

        Object[] args = new Object[1];
        args[0] = caseUUID;
        when(userService.getMaxAccessLevel(any())).thenReturn(AccessLevel.OWNER);
        when(caseService.getCase(caseUUID)).thenReturn(new CaseData(CaseDataType.MIN, 123456789L));
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        when(annotation.accessLevel()).thenReturn(AccessLevel.READ);

        aspect.validateUserAccess(proceedingJoinPoint,annotation);

        verify(annotation, times(2)).accessLevel();


    }


    @Test
    public void shouldGetRequestedPermissionTypeFromRequestWhenUNSET() throws Throwable {
        Object[] args = new Object[1];
        args[0] = caseUUID;

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(userService.getMaxAccessLevel(any())).thenReturn(AccessLevel.OWNER);
        when(caseService.getCase(caseUUID)).thenReturn(new CaseData(CaseDataType.MIN, 123456789L));
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
        when(annotation.accessLevel()).thenReturn(AccessLevel.UNSET);

        aspect.validateUserAccess(proceedingJoinPoint,annotation);

        verify(annotation, times(1)).accessLevel();

    }

}