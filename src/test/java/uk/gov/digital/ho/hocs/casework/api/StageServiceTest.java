package uk.gov.digital.ho.hocs.casework.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.casework.domain.exception.ApplicationExceptions;
import uk.gov.digital.ho.hocs.casework.domain.model.Stage;
import uk.gov.digital.ho.hocs.casework.domain.repository.StageRepository;
import uk.gov.digital.ho.hocs.casework.security.UserPermissionsService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StageServiceTest {

    private final UUID caseUUID = UUID.randomUUID();
    private final UUID teamUUID = UUID.randomUUID();
    private final UUID userUUID = UUID.randomUUID();
    private final UUID stageUUID = UUID.randomUUID();
    private final String stageType = "DCU_MIN_MARKUP";
    private final LocalDate deadline = LocalDate.now();

    @Mock
    private StageRepository stageRepository;
    private StageService stageService;
    @Mock
    private UserPermissionsService userPermissionsService;

    @Before
    public void setUp() {
        this.stageService = new StageService(stageRepository, userPermissionsService);
    }

    @Test
    public void shouldCreateStage() {

        stageService.createStage(caseUUID, stageType, teamUUID, deadline);

        verify(stageRepository, times(1)).save(any(Stage.class));

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldCreateStageNoDeadline() {

        stageService.createStage(caseUUID, stageType, teamUUID, null);

        verify(stageRepository, times(1)).save(any(Stage.class));

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldCreateStageNoUser() {

        stageService.createStage(caseUUID, stageType, teamUUID, deadline);

        verify(stageRepository, times(1)).save(any(Stage.class));

        verifyNoMoreInteractions(stageRepository);
    }

    @Test(expected = ApplicationExceptions.EntityCreationException.class)
    public void shouldNotCreateStageMissingCaseUUIDException() {

        stageService.createStage(null, stageType, teamUUID, deadline);
    }

    @Test()
    public void shouldNotCreateStageMissingCaseUUID() {

        try {
            stageService.createStage(null, stageType, teamUUID, deadline);
        } catch (ApplicationExceptions.EntityCreationException e) {
            // Do nothing.
        }

        verifyZeroInteractions(stageRepository);
    }

    @Test(expected = ApplicationExceptions.EntityCreationException.class)
    public void shouldNotCreateStageMissingTypeException() {

        stageService.createStage(caseUUID, null, teamUUID, deadline);
    }

    @Test()
    public void shouldNotCreateStageMissingType() {

        try {
            stageService.createStage(caseUUID, null, teamUUID, deadline);
        } catch (ApplicationExceptions.EntityCreationException e) {
            // Do nothing.
        }

        verifyZeroInteractions(stageRepository);
    }

    @Test
    public void shouldGetStageWithValidParams() {

        Stage stage = new Stage(caseUUID, stageType, teamUUID, deadline);

        when(stageRepository.findByUuid(caseUUID, stageUUID)).thenReturn(stage);

        stageService.getStage(caseUUID, stageUUID);

        verify(stageRepository, times(1)).findByUuid(caseUUID, stageUUID);

        verifyNoMoreInteractions(stageRepository);

    }

    @Test(expected = ApplicationExceptions.EntityNotFoundException.class)
    public void shouldNotGetStageWithValidParamsNotFoundException() {

        when(stageRepository.findByUuid(caseUUID, stageUUID)).thenReturn(null);

        stageService.getStage(caseUUID, stageUUID);
    }

    @Test
    public void shouldNotGetStageWithValidParamsNotFound() {

        when(stageRepository.findByUuid(caseUUID, stageUUID)).thenReturn(null);

        try {
            stageService.getStage(caseUUID, stageUUID);
        } catch (ApplicationExceptions.EntityNotFoundException e) {
            // Do nothing.
        }

        verify(stageRepository, times(1)).findByUuid(caseUUID, stageUUID);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test(expected = ApplicationExceptions.EntityNotFoundException.class)
    public void shouldNotGetStageMissingCaseUUIDException() {

        stageService.getStage(null, stageUUID);
    }

    @Test()
    public void shouldNotGetStageMissingCaseUUID() {

        try {
            stageService.getStage(null, stageUUID);
        } catch (ApplicationExceptions.EntityNotFoundException e) {
            // Do nothing.
        }

        verify(stageRepository, times(1)).findByUuid(null, stageUUID);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test(expected = ApplicationExceptions.EntityNotFoundException.class)
    public void shouldNotGetStageMissingStageUUIDException() {

        stageService.getStage(caseUUID, null);
    }

    @Test()
    public void shouldNotGetStageMissingStageUUID() {

        try {
            stageService.getStage(caseUUID, null);
        } catch (ApplicationExceptions.EntityNotFoundException e) {
            // Do nothing.
        }

        verify(stageRepository, times(1)).findByUuid(caseUUID, null);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldGetActiveStagesCaseUUID() {

        stageService.getActiveStagesByCaseUUID(caseUUID);

        verify(stageRepository, times(1)).findActiveStagesByCaseUUID(caseUUID);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldGetActiveStagesUserUUID() {

        stageService.getActiveStagesByUserUUID(userUUID);

        verify(stageRepository, times(1)).findAllByUserUUID(userUUID);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldGetActiveStagesUserUUIDNull() {

        stageService.getActiveStagesByUserUUID(null);

        verify(stageRepository, times(1)).findAllByUserUUID(null);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldGetActiveStagesTeamUUID() {

        stageService.getActiveStagesByTeamUUID(teamUUID);

        verify(stageRepository, times(1)).findAllByTeamUUID(teamUUID);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldGetActiveStagesTeamUUIDNull() {

        stageService.getActiveStagesByTeamUUID(null);

        verify(stageRepository, times(1)).findAllByTeamUUID(null);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldGetActiveStages() {
        Set<UUID> teams = new HashSet<>();
        teams.add(UUID.randomUUID());

        when(userPermissionsService.getUserTeams()).thenReturn(teams);

        stageService.getActiveStages();

        verify(stageRepository, times(1)).findAllBy(teams);

        verifyZeroInteractions(stageRepository);
    }

    @Test
    public void shouldGetActiveStagesEmpty() {
        Set<UUID> teams = new HashSet<>();

        when(userPermissionsService.getUserTeams()).thenReturn(teams);

        stageService.getActiveStages();

        // We don't try and get active stages with no teams (empty set) because we're going to get 0 results.

        verifyZeroInteractions(stageRepository);
    }

    @Test
    public void shouldUpdateStageDeadline() {

        Stage stage = new Stage(caseUUID, "DCU_MIN_MARKUP", teamUUID, deadline);

        when(stageRepository.findByUuid(caseUUID, stageUUID)).thenReturn(stage);

        stageService.updateDeadline(caseUUID, stageUUID, deadline);

        verify(stageRepository, times(1)).findByUuid(caseUUID, stageUUID);
        verify(stageRepository, times(1)).save(stage);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldUpdateStageDeadlineNull() {

        Stage stage = new Stage(caseUUID, "DCU_MIN_MARKUP", teamUUID, deadline);

        when(stageRepository.findByUuid(caseUUID, stageUUID)).thenReturn(stage);

        stageService.updateDeadline(caseUUID, stageUUID, null);

        verify(stageRepository, times(1)).findByUuid(caseUUID, stageUUID);
        verify(stageRepository, times(1)).save(stage);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldUpdateStageTeam() {

        Stage stage = new Stage(caseUUID, "DCU_MIN_MARKUP", teamUUID, deadline);

        when(stageRepository.findByUuid(caseUUID, stageUUID)).thenReturn(stage);

        stageService.updateTeam(caseUUID, stageUUID, teamUUID);

        verify(stageRepository, times(1)).findByUuid(caseUUID, stageUUID);
        verify(stageRepository, times(1)).save(stage);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldUpdateStageTeamNull() {

        Stage stage = new Stage(caseUUID, "DCU_MIN_MARKUP", teamUUID, deadline);

        when(stageRepository.findByUuid(caseUUID, stageUUID)).thenReturn(stage);

        stageService.updateTeam(caseUUID, stageUUID, null);

        verify(stageRepository, times(1)).findByUuid(caseUUID, stageUUID);
        verify(stageRepository, times(1)).save(stage);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldUpdateStageUser() {

        Stage stage = new Stage(caseUUID, "DCU_MIN_MARKUP", teamUUID, deadline);

        when(stageRepository.findByUuid(caseUUID, stageUUID)).thenReturn(stage);

        stageService.updateUser(caseUUID, stageUUID, userUUID);

        verify(stageRepository, times(1)).findByUuid(caseUUID, stageUUID);
        verify(stageRepository, times(1)).save(stage);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldUpdateStageUserNull() {

        Stage stage = new Stage(caseUUID, "DCU_MIN_MARKUP", teamUUID, deadline);

        when(stageRepository.findByUuid(caseUUID, stageUUID)).thenReturn(stage);

        stageService.updateUser(caseUUID, stageUUID, null);

        verify(stageRepository, times(1)).findByUuid(caseUUID, stageUUID);
        verify(stageRepository, times(1)).save(stage);

        verifyNoMoreInteractions(stageRepository);
    }

    @Test
    public void shouldCompleteStage() {

        Stage stage = new Stage(caseUUID, "DCU_MIN_MARKUP", teamUUID, deadline);

        when(stageRepository.findByUuid(caseUUID, stageUUID)).thenReturn(stage);

        stageService.completeStage(caseUUID, stageUUID);

        verify(stageRepository, times(1)).findByUuid(caseUUID, stageUUID);
        verify(stageRepository, times(1)).save(stage);

        verifyNoMoreInteractions(stageRepository);
    }
}
