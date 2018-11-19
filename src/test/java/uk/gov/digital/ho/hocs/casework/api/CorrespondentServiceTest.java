package uk.gov.digital.ho.hocs.casework.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.casework.domain.exception.EntityCreationException;
import uk.gov.digital.ho.hocs.casework.domain.exception.EntityNotFoundException;
import uk.gov.digital.ho.hocs.casework.domain.model.Address;
import uk.gov.digital.ho.hocs.casework.domain.model.Correspondent;
import uk.gov.digital.ho.hocs.casework.domain.model.CorrespondentType;
import uk.gov.digital.ho.hocs.casework.domain.repository.CorrespondentRepository;

import java.util.HashSet;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CorrespondentServiceTest {

    private final UUID caseUUID = UUID.randomUUID();
    private final UUID correspondentUUID = UUID.randomUUID();
    @Mock
    private CorrespondentRepository correspondentRepository;
    private CorrespondentService correspondentService;

    @Before
    public void setUp() {
        correspondentService = new CorrespondentService(correspondentRepository);
    }

    @Test
    public void shouldGetCorrespondents() throws EntityNotFoundException {
        HashSet<Correspondent> correspondentData = new HashSet<>();

        Address address = new Address("anyPostcode", "any1", "any2", "any3", "anyCountry");
        Correspondent correspondent = new Correspondent(caseUUID, CorrespondentType.CORRESPONDENT, "anyFullName", address, "anyPhone", "anyEmail", "anyReference");

        correspondentData.add(correspondent);

        when(correspondentRepository.findAllByCaseUUID(caseUUID)).thenReturn(correspondentData);

        correspondentService.getCorrespondents(caseUUID);

        verify(correspondentRepository, times(1)).findAllByCaseUUID(caseUUID);

        verifyNoMoreInteractions(correspondentRepository);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotGetCorrespondentsNotFoundException() {

        when(correspondentRepository.findAllByCaseUUID(caseUUID)).thenReturn(null);

        correspondentService.getCorrespondents(caseUUID);
    }

    @Test
    public void shouldNotGetCorrespondentsNotFound() {

        when(correspondentRepository.findAllByCaseUUID(caseUUID)).thenReturn(null);

        try {
            correspondentService.getCorrespondents(caseUUID);
        } catch (EntityNotFoundException e) {
            // Do nothing.
        }

        verify(correspondentRepository, times(1)).findAllByCaseUUID(caseUUID);

        verifyNoMoreInteractions(correspondentRepository);

    }

    @Test
    public void shouldNotGetCorrespondentsMissingUUIDException() throws EntityNotFoundException {

        correspondentService.getCorrespondents(null);

        verify(correspondentRepository, times(1)).findAllByCaseUUID(null);

        verifyNoMoreInteractions(correspondentRepository);
    }

    @Test
    public void shouldCreateCorrespondent() throws EntityCreationException {

        correspondentService.createCorrespondent(caseUUID, CorrespondentType.CORRESPONDENT, "anyFullName", "anyPostcode", "any1", "any2", "any3", "anyCountry", "anyPhone", "anyEmail", "anyReference");

        verify(correspondentRepository, times(1)).save(any(Correspondent.class));

        verifyNoMoreInteractions(correspondentRepository);
    }

    @Test(expected = EntityCreationException.class)
    public void shouldNotCreateCorrespondentMissingCaseUUIDException() throws EntityCreationException {
        correspondentService.createCorrespondent(null, CorrespondentType.CORRESPONDENT, "anyFullName", "anyPostcode", "any1", "any2", "any3", "anyCountry", "anyPhone", "anyEmail", "anyReference");
    }

    @Test
    public void shouldNotCreateCorrespondentMissingCaseUUID() {

        try {
            correspondentService.createCorrespondent(null, CorrespondentType.CORRESPONDENT, "anyFullName", "anyPostcode", "any1", "any2", "any3", "anyCountry", "anyPhone", "anyEmail", "anyReference");
        } catch (EntityCreationException e) {
            // Do nothing.
        }

        verifyZeroInteractions(correspondentRepository);

    }

    @Test(expected = EntityCreationException.class)
    public void shouldNotCreateCorrespondentMissingCorrespondentTypeException() throws EntityCreationException {
        correspondentService.createCorrespondent(caseUUID, null, "anyFullName", "anyPostcode", "any1", "any2", "any3", "anyCountry", "anyPhone", "anyEmail", "anyReference");
    }

    @Test
    public void shouldNotCreateCorrespondentMissingCorrespondentType() {

        try {
            correspondentService.createCorrespondent(caseUUID, null, "anyFullName", "anyPostcode", "any1", "any2", "any3", "anyCountry", "anyPhone", "anyEmail", "anyReference");
        } catch (EntityCreationException e) {
            // Do nothing.
        }

        verifyZeroInteractions(correspondentRepository);

    }

    @Test
    public void shouldGetCorrespondent() throws EntityNotFoundException {

        Address address = new Address("anyPostcode", "any1", "any2", "any3", "anyCountry");
        Correspondent correspondent = new Correspondent(caseUUID, CorrespondentType.CORRESPONDENT, "anyFullName", address, "anyPhone", "anyEmail", "anyReference");

        when(correspondentRepository.findByUUID(correspondent.getCaseUUID(), correspondent.getUuid())).thenReturn(correspondent);

        correspondentService.getCorrespondent(correspondent.getCaseUUID(), correspondent.getUuid());

        verify(correspondentRepository, times(1)).findByUUID(correspondent.getCaseUUID(), correspondent.getUuid());

        verifyNoMoreInteractions(correspondentRepository);

    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotGetCorrespondentNotFoundException() {

        when(correspondentRepository.findByUUID(caseUUID, correspondentUUID)).thenReturn(null);

        correspondentService.getCorrespondent(caseUUID, correspondentUUID);
    }

    @Test
    public void shouldNotGetCorrespondenteNotFound() {

        when(correspondentRepository.findByUUID(caseUUID, correspondentUUID)).thenReturn(null);

        try {
            correspondentService.getCorrespondent(caseUUID, correspondentUUID);
        } catch (EntityNotFoundException e) {
            // Do nothing.
        }

        verify(correspondentRepository, times(1)).findByUUID(caseUUID, correspondentUUID);

        verifyNoMoreInteractions(correspondentRepository);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotGetCorrespondentMissingCaseUUIDException() throws EntityNotFoundException {

        correspondentService.getCorrespondent(null, correspondentUUID);

    }

    @Test
    public void shouldNotGetCorrespondentMissingCaseUUID() {

        try {
            correspondentService.getCorrespondent(null, correspondentUUID);
        } catch (EntityNotFoundException e) {
            // Do nothing.
        }

        verify(correspondentRepository, times(1)).findByUUID(null, correspondentUUID);

        verifyNoMoreInteractions(correspondentRepository);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotGetCorrespondentMissingCorrespondentUUIDException() throws EntityNotFoundException {

        correspondentService.getCorrespondent(caseUUID, null);

    }

    @Test
    public void shouldNotGetCorrespondentMissingCorrespondentUUID() {

        try {
            correspondentService.getPrimaryCorrespondent(caseUUID);
        } catch (EntityNotFoundException e) {
            // Do nothing.
        }

        verify(correspondentRepository, times(1)).getPrimaryCorrespondent(caseUUID);

        verifyNoMoreInteractions(correspondentRepository);
    }

    @Test
    public void shouldGetCaseWithValidParams() throws EntityNotFoundException {

        Address address = new Address("anyPostcode", "any1", "any2", "any3", "anyCountry");
        Correspondent correspondent = new Correspondent(caseUUID, CorrespondentType.CORRESPONDENT, "anyFullName", address, "anyPhone", "anyEmail", "anyReference");

        when(correspondentRepository.getPrimaryCorrespondent(correspondent.getCaseUUID())).thenReturn(correspondent);

        correspondentService.getPrimaryCorrespondent(correspondent.getCaseUUID());

        verify(correspondentRepository, times(1)).getPrimaryCorrespondent(correspondent.getCaseUUID());

        verifyNoMoreInteractions(correspondentRepository);

    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotGetPrimaryCorrespondentNotFoundException() {

        when(correspondentRepository.getPrimaryCorrespondent(caseUUID)).thenReturn(null);

        correspondentService.getPrimaryCorrespondent(caseUUID);
    }

    @Test
    public void shouldNotGetPrimaryCorrespondentNotFound() {

        when(correspondentRepository.getPrimaryCorrespondent(caseUUID)).thenReturn(null);

        try {
            correspondentService.getPrimaryCorrespondent(caseUUID);
        } catch (EntityNotFoundException e) {
            // Do nothing.
        }

        verify(correspondentRepository, times(1)).getPrimaryCorrespondent(caseUUID);

        verifyNoMoreInteractions(correspondentRepository);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotGetPrimaryCorrespondentMissingCaseUUIDException() throws EntityNotFoundException {

        correspondentService.getPrimaryCorrespondent(null);

    }

    @Test
    public void shouldNotGetPrimaryCorrespondentMissingCaseUUID() {

        try {
            correspondentService.getPrimaryCorrespondent(null);
        } catch (EntityNotFoundException e) {
            // Do nothing.
        }

        verify(correspondentRepository, times(1)).getPrimaryCorrespondent(null);

        verifyNoMoreInteractions(correspondentRepository);
    }

    @Test
    public void shouldDeleteCase() {

        correspondentService.deleteCorrespondent(caseUUID, correspondentUUID);

        verify(correspondentRepository, times(1)).deleteCorrespondent(correspondentUUID);

        verifyNoMoreInteractions(correspondentRepository);
    }

    @Test
    public void shouldDeleteCorrespondentCaseNull() {

        correspondentService.deleteCorrespondent(null, correspondentUUID);

        verify(correspondentRepository, times(1)).deleteCorrespondent(correspondentUUID);

        verifyNoMoreInteractions(correspondentRepository);
    }

    @Test
    public void shouldDeleteCorrespondentCorrespondentNull() {

        correspondentService.deleteCorrespondent(caseUUID, null);

        verify(correspondentRepository, times(1)).deleteCorrespondent(null);

        verifyNoMoreInteractions(correspondentRepository);
    }
}