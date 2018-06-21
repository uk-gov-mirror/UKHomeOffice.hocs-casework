package uk.gov.digital.ho.hocs.casework.email;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.digital.ho.hocs.casework.email.dto.SendEmailRequest;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProxyingNotificationClientTest {

    @Mock
    private ProxyingNotificationClient mockProxyingNotificationClient;

    @Test
    public void shouldUseProxyTest() {
        String apiKey = "a key";
        String proxyHost = "somewhere";
        Integer proxyPort = 0;

        ProxyingNotificationClient proxyingNotificationClient = new ProxyingNotificationClient(apiKey, proxyHost, proxyPort);

        assertThat(proxyingNotificationClient.getProxy()).isNotNull();
    }

    @Test
    public void shouldNotUseProxyTest() {
        String apiKey = "a key";
        String proxyHost = null;
        Integer proxyPort = 0;

        ProxyingNotificationClient proxyingNotificationClient = new ProxyingNotificationClient(apiKey, proxyHost, proxyPort);

        assertThat(proxyingNotificationClient.getProxy()).isNull();
    }

    @Test
    public void shouldSendEmailNullNotifyRequest() throws NotificationClientException {
        mockProxyingNotificationClient.sendEmail(null, "tenplate");

        verify(mockProxyingNotificationClient, times(0)).sendEmail(any(), any(), any(), any());
        verify(mockProxyingNotificationClient, times(0)).sendEmail(any(), any(), any(), any(), any());

    }

    @Test
    public void shouldSendEmailNullTemplate() throws NotificationClientException {
        mockProxyingNotificationClient.sendEmail(new SendEmailRequest("a", new HashMap<>()), null);

        verify(mockProxyingNotificationClient, times(0)).sendEmail(any(), any(), any(), any());
        verify(mockProxyingNotificationClient, times(0)).sendEmail(any(), any(), any(), any(), any());

    }

    @Test
    public void shouldSendEmailNotifyException2() throws NotificationClientException {
        mockProxyingNotificationClient.sendEmail(new SendEmailRequest("a", new HashMap<>()), "t");

        verify(mockProxyingNotificationClient, times(0)).sendEmail(any(), any(), any(), anyString());
    }
}
