package uk.gov.digital.ho.hocs.casework.casedetails;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.casework.casedetails.dto.CorrespondentDto;
import uk.gov.digital.ho.hocs.casework.casedetails.exception.EntityCreationException;
import uk.gov.digital.ho.hocs.casework.casedetails.exception.EntityNotFoundException;
import uk.gov.digital.ho.hocs.casework.casedetails.model.CaseCorrespondent;
import uk.gov.digital.ho.hocs.casework.casedetails.model.CorrespondentData;
import uk.gov.digital.ho.hocs.casework.casedetails.repository.CaseCorrespondentRepository;
import uk.gov.digital.ho.hocs.casework.casedetails.repository.CorrespondentDataRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CorrespondentDataServiceTest {

    @Mock
    private CorrespondentDataRepository correspondentDataRepository;

    @Mock
    private CaseCorrespondentRepository caseCorrespondentRepository;

    private CorrespondentDataService correspondentDataService;

    @Before
    public void setUp() {
        this.correspondentDataService = new CorrespondentDataService(
                correspondentDataRepository,
                caseCorrespondentRepository
        );
    }

    @Test
    public void shouldSaveCorrespondentData() {
        UUID caseUUID = UUID.randomUUID();
        CorrespondentDto correspondentDto =
                new CorrespondentDto(null,"Mr",
                        "Bob",
                        "Smith",
                        "S1 1DJ",
                        "1 somewhere street",
                        "some",
                        "Where",
                        "UK",
                        "01234 567890",
                        "A@A.com",
                        "Complainant");

        correspondentDataService.addCorrespondentToCase(caseUUID, correspondentDto);
        verify(correspondentDataRepository, times(1)).save(any(CorrespondentData.class));
        verify(caseCorrespondentRepository, times(1)).save(any(CaseCorrespondent.class));

        verifyNoMoreInteractions(correspondentDataRepository);
        verifyNoMoreInteractions(caseCorrespondentRepository);
    }


    @Test
    public void shouldGetAllCorrespondenceForIndividualCase(){
        UUID uuid = UUID.randomUUID();
        Set<CorrespondentData> correspondents = new HashSet<>();
        CorrespondentData correspondentData =
                new CorrespondentData(1,
                        uuid,
                        "Mr",
                        "Bob",
                        "Smith",
                        "S1 1DJ",
                        "1 somewhere street",
                        "some",
                        "Where",
                        "UK",
                        "01234 567890",
                        "A@A.com",
                        LocalDateTime.now(),
                        null,
                        "Complainant");
        when(correspondentDataService.getCorrespondents(uuid)).thenReturn(correspondents);
        Set<CorrespondentData> response = correspondentDataService.getCorrespondents(uuid);

        verify(correspondentDataRepository, times(1)).findByCaseUUID(uuid);
        verifyNoMoreInteractions(correspondentDataRepository);
        verifyNoMoreInteractions(caseCorrespondentRepository);
    }

}