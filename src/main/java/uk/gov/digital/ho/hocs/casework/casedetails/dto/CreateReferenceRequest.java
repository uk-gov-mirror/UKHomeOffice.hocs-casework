package uk.gov.digital.ho.hocs.casework.casedetails.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.digital.ho.hocs.casework.casedetails.model.ReferenceType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateReferenceRequest {

    @JsonProperty("type")
    private ReferenceType type;

    @JsonProperty("reference")
    private String reference;
}
