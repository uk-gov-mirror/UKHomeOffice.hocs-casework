package uk.gov.digital.ho.hocs.casework.notify;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.casework.model.NotifyDetails;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.UUID;


@Service
@Slf4j
public class NotifyService {

    private final String rshTemplateId;
    private final String frontEndUrl;

    private final NotificationClient client;

    @Autowired
    public NotifyService(@Value("${notify.apiKey}") String apiKey,
                         @Value("${notify.rshTemplateId}") String rshTemplateId,
                         @Value("${notify.frontend.url}") String frontEndUrl) {
        this.rshTemplateId = rshTemplateId;
        this.frontEndUrl = frontEndUrl;
        client = new NotificationClient(apiKey);
    }

    public void sendRshNotify(NotifyDetails notifyDetails, UUID caseUUID) {
        if (notifyDetails.getNotifyEmail() != null && !notifyDetails.getNotifyEmail().isEmpty()) {
            sendEmail(notifyDetails.getNotifyEmail(), notifyDetails.getNotifyTeamName(),caseUUID, rshTemplateId);
        }
    }

    public void sendNotify(NotifyDetails notifyDetails, UUID caseUUID) {
        if (notifyDetails.getNotifyEmail() != null && !notifyDetails.getNotifyEmail().isEmpty()) {
            sendEmail(notifyDetails.getNotifyEmail(), notifyDetails.getNotifyTeamName(),caseUUID, "");
        }
    }

    private void sendEmail(String emailAddress, String teamName,UUID caseUUID, String templateId) {
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put("team", teamName);
        personalisation.put("link", frontEndUrl + "/rsh/case/" + caseUUID);
        try {
            client.sendEmail(templateId, emailAddress, personalisation, null, null);
        } catch (NotificationClientException e) {
            e.printStackTrace();
        }
    }
}

