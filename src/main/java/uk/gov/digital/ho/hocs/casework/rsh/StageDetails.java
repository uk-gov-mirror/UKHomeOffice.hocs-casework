package uk.gov.digital.ho.hocs.casework.rsh;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "stage_details")
@Getter
@NoArgsConstructor
public class StageDetails implements Serializable {

    public StageDetails(UUID caseUUID, String name, int schemaVersion, String data) {
        LocalDateTime now = LocalDateTime.now();
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.data = data;
        this.caseUUID = caseUUID;
        this.schemaVersion = schemaVersion;
        this.created = now;
        this.updated = now;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name ="uuid")
    private UUID uuid;

    @Column(name ="name")
    private String name;

    @Setter
    @Column(name ="data")
    private String data;

    @Column(name = "case_uuid")
    private UUID caseUUID;

    @Setter
    @Column(name = "schema_version")
    private int schemaVersion;

    @Column(name = "created")
    private LocalDateTime created;

    @Setter
    @Column(name = "updated")
    private LocalDateTime updated;
}
