package uk.gov.digital.ho.hocs.casework.caseDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "audit_case_data")
@Getter
@NoArgsConstructor
public class AuditCaseData implements Serializable {

    private AuditCaseData(String type, String reference, UUID uuid, LocalDateTime created, Set<AuditStageData> stages) {
        this.type = type;
        this.reference = reference;
        this.uuid = uuid;
        this.created = created;
        this.stages = stages;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type")
    private String type;

    @Column(name = "reference")
    private String reference;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "created")
    private LocalDateTime created;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name ="case_uuid", referencedColumnName = "uuid")
    private Set<AuditStageData> stages = new HashSet<>();

    public static AuditCaseData from(CaseDetails caseDetails) {
        Set<AuditStageData> stageAudits = caseDetails.getStages().stream().map(AuditStageData::from).collect(Collectors.toSet());
        return new AuditCaseData(caseDetails.getType(), caseDetails.getReference(), caseDetails.getUuid(), caseDetails.getCreated(), stageAudits);
    }

}
