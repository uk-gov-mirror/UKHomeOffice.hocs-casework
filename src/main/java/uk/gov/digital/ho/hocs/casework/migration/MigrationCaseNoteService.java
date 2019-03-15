package uk.gov.digital.ho.hocs.casework.migration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.digital.ho.hocs.casework.client.auditclient.AuditClient;
import uk.gov.digital.ho.hocs.casework.domain.model.CaseNote;
import uk.gov.digital.ho.hocs.casework.domain.repository.CaseNoteRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;
import static uk.gov.digital.ho.hocs.casework.application.LogEvent.*;

@Service
@Slf4j
public class MigrationCaseNoteService {

    private final CaseNoteRepository caseNoteRepository;
    private final AuditClient auditClient;

    @Autowired
    public MigrationCaseNoteService(CaseNoteRepository caseNoteRepository, AuditClient auditClient) {
        this.caseNoteRepository = caseNoteRepository;
        this.auditClient = auditClient;
    }

    public UUID migrationCaseNote(UUID caseUUID, LocalDateTime timestamp, String user, String text) {
        log.debug("Migrating CaseNote for Case: {}", caseUUID);
        CaseNote caseNote = new CaseNote(caseUUID, "MANUAL", timestamp, text);
        caseNoteRepository.save(caseNote);
        log.info("Created CaseNote: {} for Case: {}", caseNote.getUuid(), caseUUID, value(EVENT, CASE_NOTE_CREATED));
        auditClient.createCaseNoteAudit(caseNote);
        return caseNote.getUuid();
    }
}