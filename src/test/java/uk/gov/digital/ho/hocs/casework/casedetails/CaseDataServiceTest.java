package uk.gov.digital.ho.hocs.casework.casedetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.casework.audit.AuditService;
import uk.gov.digital.ho.hocs.casework.casedetails.dto.DeadlineDataDto;
import uk.gov.digital.ho.hocs.casework.casedetails.exception.EntityCreationException;
import uk.gov.digital.ho.hocs.casework.casedetails.exception.EntityNotFoundException;
import uk.gov.digital.ho.hocs.casework.casedetails.model.CaseData;
import uk.gov.digital.ho.hocs.casework.casedetails.model.CaseType;
import uk.gov.digital.ho.hocs.casework.casedetails.model.InputData;
import uk.gov.digital.ho.hocs.casework.casedetails.model.DeadlineData;
import uk.gov.digital.ho.hocs.casework.casedetails.repository.CaseDataRepository;
import uk.gov.digital.ho.hocs.casework.casedetails.repository.InputDataRepository;
import uk.gov.digital.ho.hocs.casework.casedetails.repository.DeadlineDataRepository;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseDataServiceTest {

    @Mock
    private CaseDataRepository caseDataRepository;

    @Mock
    private StageDataService stageDataService;

    @Mock
    private InputDataRepository inputDataRepository;

    private InputDataService inputDataService;


    @Mock
    private AuditService auditService;

    private CaseDataService caseDataService;

    @Before
    public void setUp() {
        this.inputDataService = new InputDataService(inputDataRepository, auditService, new ObjectMapper());

        this.caseDataService = new CaseDataService(
                caseDataRepository,
                inputDataService,
                auditService,
                stageDataService);
    }

    @Test
    public void shouldCreateCaseWithValidParams() throws EntityCreationException {
        Long caseID = 12345L;

        when(caseDataRepository.getNextSeriesId()).thenReturn(caseID);

        CaseData caseData = caseDataService.createCase(CaseType.MIN, LocalDate.now());

        verify(caseDataRepository, times(1)).getNextSeriesId();
        verify(caseDataRepository, times(1)).save(caseData);
        verify(inputDataRepository, times(1)).save(any(InputData.class));
        verify(auditService, times(1)).createCaseEvent(caseData);
        verify(auditService, times(1)).createInputDataEvent(any(InputData.class));

        verifyNoMoreInteractions(caseDataRepository);
        verifyNoMoreInteractions(inputDataRepository);
        verifyNoMoreInteractions(auditService);
        verifyZeroInteractions(stageDataService);
    }

    @Test(expected = EntityCreationException.class)
    public void shouldNotCreateCaseMissingTypeException() throws EntityCreationException {
        caseDataService.createCase(null, null);
    }

    @Test()
    public void shouldNotCreateCaseMissingType() {
        Long caseID = 12345L;

        when(caseDataRepository.getNextSeriesId()).thenReturn(caseID);

        try {
            caseDataService.createCase(null, null);
        } catch (EntityCreationException e) {
            // Do nothing.
        }

        verify(caseDataRepository, times(1)).getNextSeriesId();

        verifyNoMoreInteractions(caseDataRepository);
        verifyZeroInteractions(inputDataRepository);
        verifyZeroInteractions(auditService);
        verifyZeroInteractions(stageDataService);

    }

    @Test
    public void shouldGetCaseWithValidParams() throws EntityNotFoundException {
        Long caseID = 12345L;
        CaseData caseData = new CaseData(CaseType.MIN, caseID);
        InputData inputData = new InputData(caseData.getUuid());

        when(caseDataRepository.findByUuid(caseData.getUuid())).thenReturn(caseData);
        when(inputDataRepository.findByCaseUUID(caseData.getUuid())).thenReturn(inputData);


        caseDataService.getCase(caseData.getUuid());

        verify(caseDataRepository, times(1)).findByUuid(caseData.getUuid());
        verify(inputDataRepository, times(1)).findByCaseUUID(caseData.getUuid());
        verify(auditService, times(1)).getCaseEvent(caseData.getUuid());
        verify(stageDataService, times(1)).getStagesForCase(caseData.getUuid());

        verifyNoMoreInteractions(caseDataRepository);
        verifyNoMoreInteractions(inputDataRepository);
        verifyNoMoreInteractions(auditService);
        verifyZeroInteractions(stageDataService);

    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotGetCaseWithValidParamsNotFoundException() {
        CaseData caseData = new CaseData(CaseType.MIN, 0l);

        when(caseDataRepository.findByUuid(caseData.getUuid())).thenReturn(null);

        caseDataService.getCase(caseData.getUuid());
    }

    @Test
    public void shouldNotGetCaseWithValidParamsNotFound() {
        UUID uuid = UUID.randomUUID();

        when(caseDataRepository.findByUuid(uuid)).thenReturn(null);

        try {
            caseDataService.getCase(uuid);
        } catch (EntityNotFoundException e) {
            // Do nothing.
        }

        verify(caseDataRepository, times(1)).findByUuid(uuid);

        verifyNoMoreInteractions(caseDataRepository);
        verifyZeroInteractions(inputDataRepository);
        verifyZeroInteractions(auditService);
        verifyZeroInteractions(stageDataService);

    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldNotGetCaseMissingUUIDException() throws EntityNotFoundException {

        caseDataService.getCase(null);

    }

    @Test
    public void shouldNotGetCaseMissingUUID() {

        try {
            caseDataService.getCase(null);
        } catch (EntityNotFoundException e) {
            // Do nothing.
        }

        verify(caseDataRepository, times(1)).findByUuid(null);

        verifyNoMoreInteractions(caseDataRepository);
        verifyZeroInteractions(inputDataRepository);
        verifyZeroInteractions(auditService);
        verifyZeroInteractions(stageDataService);
    }


}
