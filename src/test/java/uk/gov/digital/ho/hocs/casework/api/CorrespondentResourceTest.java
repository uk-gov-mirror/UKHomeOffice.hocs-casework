package uk.gov.digital.ho.hocs.casework.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.digital.ho.hocs.casework.api.dto.CorrespondentDto;
import uk.gov.digital.ho.hocs.casework.api.dto.GetCorrespondentsResponse;
import uk.gov.digital.ho.hocs.casework.domain.model.Address;
import uk.gov.digital.ho.hocs.casework.domain.model.Correspondent;
import uk.gov.digital.ho.hocs.casework.domain.model.CorrespondentType;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CorrespondentResourceTest {

    private final UUID caseUUID = UUID.randomUUID();
    private final UUID correspondentUUID = UUID.randomUUID();
    @Mock
    private CorrespondentService correspondentService;
    private CorrespondentResource correspondentResource;

    @Before
    public void setUp() {
        correspondentResource = new CorrespondentResource(correspondentService);
    }


    @Test
    public void shouldGetCorrespondents() {

        when(correspondentService.getCorrespondents(caseUUID)).thenReturn(new HashSet<>());

        ResponseEntity<GetCorrespondentsResponse> response = correspondentResource.getCorrespondents(caseUUID);

        verify(correspondentService, times(1)).getCorrespondents(caseUUID);

        verifyNoMoreInteractions(correspondentService);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldGetCorrespondent() {

        Address address = new Address("anyPostcode", "any1", "any2", "any3", "anyCountry");
        Correspondent correspondent = new Correspondent(caseUUID, CorrespondentType.CORRESPONDENT, "anyFullName", address, "anyPhone", "anyEmail", "anyReference");

        when(correspondentService.getCorrespondent(caseUUID, correspondentUUID)).thenReturn(correspondent);

        ResponseEntity<CorrespondentDto> response = correspondentResource.getCorrespondent(caseUUID, correspondentUUID);

        verify(correspondentService, times(1)).getCorrespondent(caseUUID, correspondentUUID);

        verifyNoMoreInteractions(correspondentService);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldGetPrimaryCorrespondent() {

        Address address = new Address("anyPostcode", "any1", "any2", "any3", "anyCountry");
        Correspondent correspondent = new Correspondent(caseUUID, CorrespondentType.CORRESPONDENT, "anyFullName", address, "anyPhone", "anyEmail", "anyReference");

        when(correspondentService.getPrimaryCorrespondent(caseUUID)).thenReturn(correspondent);

        ResponseEntity<CorrespondentDto> response = correspondentResource.getPrimaryCorrespondent(caseUUID);

        verify(correspondentService, times(1)).getPrimaryCorrespondent(caseUUID);

        verifyNoMoreInteractions(correspondentService);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldDeleteCorrespondentFromCase() {

        doNothing().when(correspondentService).deleteCorrespondent(caseUUID, correspondentUUID);

        ResponseEntity response = correspondentResource.deleteCorrespondent(caseUUID, correspondentUUID);

        verify(correspondentService, times(1)).deleteCorrespondent(caseUUID, correspondentUUID);

        verifyNoMoreInteractions(correspondentService);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}