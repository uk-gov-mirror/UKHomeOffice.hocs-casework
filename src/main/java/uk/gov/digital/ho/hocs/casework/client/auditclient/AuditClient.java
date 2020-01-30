package uk.gov.digital.ho.hocs.casework.client.auditclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import uk.gov.digital.ho.hocs.casework.application.RequestData;
import uk.gov.digital.ho.hocs.casework.application.RequestDataDto;
import uk.gov.digital.ho.hocs.casework.application.RestHelper;
import uk.gov.digital.ho.hocs.casework.client.auditclient.dto.AuditPayload;
import uk.gov.digital.ho.hocs.casework.client.auditclient.dto.CreateAuditRequest;
import uk.gov.digital.ho.hocs.casework.client.auditclient.dto.GetAuditListResponse;
import uk.gov.digital.ho.hocs.casework.client.auditclient.dto.GetAuditResponse;
import uk.gov.digital.ho.hocs.casework.domain.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.casework.application.LogEvent.*;
import static uk.gov.digital.ho.hocs.casework.client.auditclient.EventType.CASE_CREATED;
import static uk.gov.digital.ho.hocs.casework.client.auditclient.EventType.CASE_TOPIC_DELETED;
import static uk.gov.digital.ho.hocs.casework.client.auditclient.EventType.CASE_UPDATED;
import static uk.gov.digital.ho.hocs.casework.client.auditclient.EventType.CORRESPONDENT_CREATED;
import static uk.gov.digital.ho.hocs.casework.client.auditclient.EventType.CORRESPONDENT_DELETED;
import static uk.gov.digital.ho.hocs.casework.client.auditclient.EventType.STAGE_COMPLETED;
import static uk.gov.digital.ho.hocs.casework.client.auditclient.EventType.STAGE_CREATED;
import static uk.gov.digital.ho.hocs.casework.client.auditclient.EventType.STAGE_RECREATED;
import static uk.gov.digital.ho.hocs.casework.client.auditclient.EventType.*;

@Slf4j
@Component
public class AuditClient {

    private final String auditQueue;
    private final String raisingService;
    private final String namespace;
    private final ProducerTemplate producerTemplate;
    private final ObjectMapper objectMapper;
    private final RequestData requestData;
    private final RestHelper restHelper;
    private final String serviceBaseURL;
    private final String EVENT_TYPE_HEADER = "event_type";

    private ExecutorService executorService;


    @Autowired
    public AuditClient(ProducerTemplate producerTemplate,
                       @Value("${audit.sns}") String auditQueue,
                       @Value("${auditing.deployment.name}") String raisingService,
                       @Value("${auditing.deployment.namespace}") String namespace,
                       ObjectMapper objectMapper,
                       RequestData requestData,
                       RestHelper restHelper,
                       @Value("${hocs.audit-service}") String auditService) {
        this.producerTemplate = producerTemplate;
        this.auditQueue = auditQueue;
        this.raisingService = raisingService;
        this.namespace = namespace;
        this.objectMapper = objectMapper;
        this.requestData = requestData;
        this.restHelper = restHelper;
        this.serviceBaseURL = auditService;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void updateCaseAudit(CaseData caseData, UUID stageUUID) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            String data = "{}";
            try {
                data = objectMapper.writeValueAsString(AuditPayload.UpdateCaseRequest.from(caseData));
            } catch (JsonProcessingException e) {
                log.error("Failed to parse data payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
            sendAuditMessage(caseData.getUuid(), data, CASE_UPDATED, stageUUID, data, requestDataDto.getCorrelationId(),
                    requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });
    }

    public void viewCaseAudit(CaseData caseData) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            try {
                sendAuditMessage(caseData.getUuid(), objectMapper.writeValueAsString(new AuditPayload.CaseReference(caseData.getReference())),
                        EventType.CASE_VIEWED, null, requestDataDto.getCorrelationId(), requestDataDto.getUserId(),
                        requestDataDto.getUsername(), requestDataDto.getGroups());
            } catch (JsonProcessingException e) {
                log.error("Failed to parse audit payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
        });
    }

    public void viewCaseSummaryAudit(CaseData caseData, CaseSummary caseSummary) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            try {
                sendAuditMessage(caseData.getUuid(), objectMapper.writeValueAsString(new AuditPayload.CaseReference(caseData.getReference())),
                        EventType.CASE_SUMMARY_VIEWED, null, requestDataDto.getCorrelationId(), requestDataDto.getUserId(),
                        requestDataDto.getUsername(), requestDataDto.getGroups());
            } catch (JsonProcessingException e) {
                log.error("Failed to parse audit payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
        });
    }

    public void viewStandardLineAudit(CaseData caseData) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            try {
                sendAuditMessage(caseData.getUuid(), objectMapper.writeValueAsString(new AuditPayload.CaseReference(caseData.getReference())),
                        EventType.STANDARD_LINE_VIEWED, null, requestDataDto.getCorrelationId(), requestDataDto.getUserId(),
                        requestDataDto.getUsername(), requestDataDto.getGroups());
            } catch (JsonProcessingException e) {
                log.error("Failed to parse audit payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
        });
    }

    public void viewTemplateAudit(CaseData caseData) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            try {
                sendAuditMessage(caseData.getUuid(), objectMapper.writeValueAsString(new AuditPayload.CaseReference(caseData.getReference())),
                        EventType.TEMPLATE_VIEWED, null, requestDataDto.getCorrelationId(), requestDataDto.getUserId(),
                        requestDataDto.getUsername(), requestDataDto.getGroups());
            } catch (JsonProcessingException e) {
                log.error("Failed to parse audit payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
        });
    }

    public void deleteCaseAudit(CaseData caseData) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            String data = "{}";
            try {
                data = objectMapper.writeValueAsString(new AuditPayload.Case(caseData.getUuid()));
            } catch (JsonProcessingException e) {
                log.error("Failed to parse data payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
            sendAuditMessage(caseData.getUuid(), data, EventType.CASE_DELETED, null, data, requestDataDto.getCorrelationId(),
                    requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });
    }

    public void completeCaseAudit(CaseData caseData) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            String data = "{}";
            try {
                data = objectMapper.writeValueAsString(new AuditPayload.Case(caseData.getUuid()));
            } catch (JsonProcessingException e) {
                log.error("Failed to parse data payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
            sendAuditMessage(caseData.getUuid(), data, EventType.CASE_COMPLETED, null, data, requestDataDto.getCorrelationId(),
                    requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });
    }

    public void viewCaseNotesAudit(UUID caseUUID, Set<CaseNote> caseNotes) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            sendAuditMessage(caseUUID, "", EventType.CASE_NOTES_VIEWED, null, requestDataDto.getCorrelationId(),
                    requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });
    }

    public void viewCaseNoteAudit(CaseNote caseNote) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            sendAuditMessage(caseNote.getCaseUUID(), "", EventType.CASE_NOTE_VIEWED, null, requestDataDto.getCorrelationId(),
                    requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });
    }

    public void createCaseNoteAudit(CaseNote caseNote) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            sendAuditMessage(caseNote.getCaseUUID(), "", EventType.CASE_NOTE_CREATED, null,
                    requestDataDto.getCorrelationId(), requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });
    }

    public void createCorrespondentAudit(Correspondent correspondent) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            String data = "{}";
            try {
                data = objectMapper.writeValueAsString(AuditPayload.CreateCorrespondentRequest.from(correspondent));
            } catch (JsonProcessingException e) {
                log.error("Failed to parse data payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
            sendAuditMessage(correspondent.getCaseUUID(), data, CORRESPONDENT_CREATED, null, data,
                    requestDataDto.getCorrelationId(), requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });
    }

    public void deleteCorrespondentAudit(Correspondent correspondent) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            String data = String.format("{\"uuid\":\"%s\"}", correspondent.getUuid());
            sendAuditMessage(correspondent.getCaseUUID(), "", CORRESPONDENT_DELETED, null, data,
                    requestDataDto.getCorrelationId(), requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });
    }

    public void createTopicAudit(Topic topic) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            String data = "{}";
            try {
                data = objectMapper.writeValueAsString(new AuditPayload.Topic(topic.getTextUUID(), topic.getText()));
            } catch (JsonProcessingException e) {
                log.error("Failed to parse audit payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
            sendAuditMessage(topic.getCaseUUID(), data, CASE_TOPIC_CREATED, null, data, requestDataDto.getCorrelationId(),
                    requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });

    }

    public void deleteTopicAudit(Topic topic) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            String data = "{}";
            try {
                data = objectMapper.writeValueAsString(new AuditPayload.Topic(topic.getTextUUID(), topic.getText()));
            } catch (JsonProcessingException e) {
                log.error("Failed to parse audit payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
            sendAuditMessage(topic.getCaseUUID(), data, CASE_TOPIC_DELETED, null, data, requestDataDto.getCorrelationId(),
                    requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });
    }

    public void createCaseAudit(CaseData caseData) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            String data = "{}";
            try {
                data = objectMapper.writeValueAsString(AuditPayload.CreateCaseRequest.from(caseData));
            } catch (JsonProcessingException e) {
                log.error("Failed to parse data payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
            sendAuditMessage(caseData.getUuid(), data, CASE_CREATED, null, data, requestDataDto.getCorrelationId(),
                    requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
        });
    }

    public void updateStageUser(Stage stage) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            try {
                EventType allocationType;
                if (stage.getUserUUID() != null) {
                    allocationType = STAGE_ALLOCATED_TO_USER;
                } else {
                    allocationType = STAGE_UNALLOCATED_FROM_USER;
                }
                sendAuditMessage(stage.getCaseUUID(), objectMapper.writeValueAsString(new AuditPayload.StageAllocation(stage.getUuid(),
                        stage.getUserUUID(), stage.getStageType(), null)), allocationType, stage.getUuid(),
                        requestDataDto.getCorrelationId(), requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
            } catch (JsonProcessingException e) {
                log.error("Failed to parse audit payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
        });
    }

    public void createStage(Stage stage) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            try {
                sendAuditMessage(stage.getCaseUUID(), objectMapper.writeValueAsString(new AuditPayload.StageAllocation(stage.getUuid(),
                                stage.getTeamUUID(), stage.getStageType(), stage.getDeadline())), STAGE_CREATED, stage.getUuid(),
                                requestDataDto.getCorrelationId(), requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
            } catch (JsonProcessingException e) {
                log.error("Failed to parse audit payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
        });
    }

    public void recreateStage(Stage stage) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);
        executorService.execute(() -> {
            try {
                sendAuditMessage(stage.getCaseUUID(), objectMapper.writeValueAsString(new AuditPayload.StageAllocation(stage.getUuid(),
                                stage.getTeamUUID(), stage.getStageType(), null)), STAGE_RECREATED, stage.getUuid(), requestDataDto.getCorrelationId(),
                                requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
            } catch (JsonProcessingException e) {
                log.error("Failed to parse audit payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
        });
    }

    public void updateStageTeam(Stage stage) {
        RequestDataDto requestDataDto = RequestDataDto.from(requestData);

        executorService.execute(() -> {
            try {
                EventType allocationType;
                if (stage.getTeamUUID() != null) {
                    allocationType = STAGE_ALLOCATED_TO_TEAM;
                } else {
                    allocationType = STAGE_COMPLETED;
                }
                sendAuditMessage(stage.getCaseUUID(), objectMapper.writeValueAsString(new AuditPayload.StageAllocation(stage.getUuid(), stage.getTeamUUID(), stage.getStageType(), stage.getDeadline())), allocationType,
                                 stage.getUuid(), requestDataDto.getCorrelationId(), requestDataDto.getUserId(), requestDataDto.getUsername(), requestDataDto.getGroups());
            } catch (JsonProcessingException e) {
                log.error("Failed to parse audit payload, event {}, exception: {}", value(EVENT, UNCAUGHT_EXCEPTION), value(EXCEPTION, e));
            }
        });
    }

    private void sendAuditMessage(UUID caseUUID, String payload, EventType eventType, UUID stageUUID, String correlationId, String userId, String username, String groups) {
        sendAuditMessage(caseUUID, payload, eventType, stageUUID, "{}", correlationId, userId, username, groups);
    }

    @Retryable(maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.delay}"))
    private void sendAuditMessage(UUID caseUUID, String payload, EventType eventType, UUID stageUUID, String data, String correlationId, String userId, String username, String groups) {
        CreateAuditRequest request = new CreateAuditRequest(
                correlationId,
                caseUUID,
                stageUUID,
                raisingService,
                payload,
                data,
                namespace,
                LocalDateTime.now(),
                eventType,
                userId);

        try {
            Map<String, Object> queueHeaders = getQueueHeaders(eventType.toString(), correlationId, userId, username, groups);
            producerTemplate.sendBodyAndHeaders(auditQueue, objectMapper.writeValueAsString(request), queueHeaders);
            log.info("Create audit of type {} for Case UUID: {}, correlationID: {}, UserID: {}, event: {}", eventType, caseUUID, correlationId, userId, value(EVENT, AUDIT_EVENT_CREATED));
        } catch (Exception e) {
            log.error("Failed to create audit event for case UUID {}, event {}, exception: {}", caseUUID, value(EVENT, AUDIT_FAILED), value(EXCEPTION, e));
        }
    }

    public Set<GetAuditResponse> getAuditLinesForCase(UUID caseUUID, List<String> requestedEvents) {
        try {
            String events = String.join(",", requestedEvents);
            GetAuditListResponse response = restHelper.get(serviceBaseURL, String.format("/audit/case/%s?types=%s", caseUUID, events), GetAuditListResponse.class);
            log.info("Got {} audits", response.getAudits().size(), value(EVENT, AUDIT_CLIENT_GET_AUDITS_FOR_CASE_SUCCESS));
            return response.getAudits();
        } catch (RestClientException e) {
            log.error("Could not get audit lines, event {}, exception: {}", value(EVENT, AUDIT_CLIENT_GET_AUDITS_FOR_CASE_FAILURE), value(EXCEPTION, e));
            return new HashSet<>();
        }
    }

    private Map<String, Object> getQueueHeaders(String eventType, String correlationId, String userId, String username, String groups) {
        return Map.of(
                EVENT_TYPE_HEADER, eventType,
                RequestData.CORRELATION_ID_HEADER, correlationId,
                RequestData.USER_ID_HEADER, userId,
                RequestData.USERNAME_HEADER, username,
                RequestData.GROUP_HEADER, groups);
    }


}
