package uk.gov.digital.ho.hocs.casework.client.notifiyclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.casework.client.infoclient.InfoClient;
import uk.gov.digital.ho.hocs.casework.client.infoclient.InfoNominatedPeople;
import uk.gov.digital.ho.hocs.casework.client.infoclient.UserDto;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class NotifyClient {

    private final NotificationClient notifyClient;
    private final InfoClient infoClient;
    private final String URL;

    @Autowired
    public NotifyClient(InfoClient infoClient,
                        @Value("${notify.apiKey}") String apiKey,
                        @Value("${hocs.url}") String URL) {
        this.notifyClient = new NotificationClient(apiKey);
        this.infoClient = infoClient;
        this.URL = URL;
    }

    public void sendTeamEmail(UUID caseUUID, UUID stageUUID, UUID teamUUID, String caseReference, String allocationType) {
        Set<InfoNominatedPeople> nominatedPeople = infoClient.getNominatedPeople(teamUUID);
        NotifyType notifyType = NotifyType.valueOf(allocationType);
        for (InfoNominatedPeople contact : nominatedPeople) {
            sendEmail(caseUUID, stageUUID, contact.getEmailAddress(), "Team", caseReference, notifyType);
        }
    }

    public void sendUserEmail(UUID caseUUID, UUID stageUUID, UUID currentUserUUID, UUID newUserUUID, String caseReference) {
        if (newUserUUID != null) {
            if (currentUserUUID != null && !newUserUUID.equals(currentUserUUID)) {
                sendUnAllocateUserEmail(caseUUID, stageUUID, currentUserUUID, caseReference);
                sendAllocateUserEmail(caseUUID, stageUUID, newUserUUID, caseReference);
            } else {
                sendAllocateUserEmail(caseUUID, stageUUID, newUserUUID, caseReference);
            }
        } else if (currentUserUUID != null) {
            sendUnAllocateUserEmail(caseUUID, stageUUID, currentUserUUID, caseReference);
        }
    }

    private void sendAllocateUserEmail(UUID caseUUID, UUID stageUUID, UUID userUUID, String caseReference) {
        UserDto user = infoClient.getUser(userUUID);
        sendEmail(caseUUID, stageUUID, user.getEmail(), user.getFirstName(), caseReference, NotifyType.ALLOCATE_INDIVIDUAL);
    }

    private void sendUnAllocateUserEmail(UUID caseUUID, UUID stageUUID, UUID userUUID, String caseReference) {
        UserDto user = infoClient.getUser(userUUID);
        sendEmail(caseUUID, stageUUID, user.getEmail(), user.getFirstName(), caseReference, NotifyType.UNALLOCATE_INDIVIDUAL);
    }

    private void sendEmail(UUID caseUUID, UUID stageUUID, String emailAddress, String firstname, String caseReference, NotifyType notifyType) {
        String link = String.format("%s/case/%s/stage/%s", URL, caseUUID, stageUUID);
        Map<String, String> personalisation = new HashMap<>();
        personalisation.put("link", link);
        personalisation.put("caseRef", caseReference);
        personalisation.put("user", firstname);
        sendEmail(notifyType, emailAddress, personalisation);
    }

    private void sendEmail(NotifyType notifyType, String emailAddress, Map<String, String> personalisation) {
        log.info("Sending email to {}, template ID {}", emailAddress, notifyType.getDisplayValue());

        try {

            notifyClient.sendEmail(notifyType.getDisplayValue(), emailAddress, personalisation, null);
        } catch (NotificationClientException e) {
            log.error(e.getLocalizedMessage());
            log.warn("Didn't send email to {}", emailAddress);
        }

    }
}