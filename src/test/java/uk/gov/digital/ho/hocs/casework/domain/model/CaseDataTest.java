package uk.gov.digital.ho.hocs.casework.domain.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.digital.ho.hocs.casework.domain.exception.EntityCreationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CaseDataTest {

    @Test
    public void getCaseData() {
        CaseDataType type = CaseDataType.MIN;
        Long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        CaseData caseData = new CaseData(type, caseNumber, data, objectMapper, caseDeadline);

        assertThat(caseData.getUuid()).isOfAnyClassIn(UUID.randomUUID().getClass());
        assertThat(caseData.getCreated()).isOfAnyClassIn(LocalDateTime.now().getClass());
        assertThat(caseData.getCaseDataType()).isEqualTo(type);
        assertThat(caseData.getData()).isEqualTo("{}");
        assertThat(caseData.isPriority()).isEqualTo(false);
        assertThat(caseData.getPrimaryCorrespondentUUID()).isEqualTo(null);
        assertThat(caseData.getPrimaryTopicUUID()).isEqualTo(null);
        assertThat(caseData.getCaseDeadline()).isEqualTo(caseDeadline);

    }

    @Test
    public void getCaseDataReferenceFormat() {

        CaseDataType type = CaseDataType.MIN;
        long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        CaseData caseData = new CaseData(type, caseNumber, data, objectMapper, caseDeadline);

        assertThat(caseData.getReference()).matches("[A-Z]{3,4}/[0-9]{7}/[0-9]{2}");

        assertThat(caseData.getReference()).startsWith(type.getDisplayValue());
        assertThat(caseData.getReference()).contains(String.valueOf(caseNumber));
        assertThat(caseData.getReference()).endsWith(String.valueOf(caseData.getCreated().getYear()).substring(2, 4));

    }

    @Test(expected = EntityCreationException.class)
    public void getCaseDataNullType() {

        Long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        new CaseData(null, caseNumber, data, objectMapper, caseDeadline);
    }

    @Test(expected = EntityCreationException.class)
    public void getCaseDataNullNumber() {

        CaseDataType type = CaseDataType.MIN;
        Map<String, String> data = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        new CaseData(type, null, data, objectMapper, caseDeadline);
    }

    @Test
    public void update() {

        CaseDataType type = CaseDataType.MIN;
        Long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        CaseData caseData = new CaseData(type, caseNumber, data, objectMapper, caseDeadline);

        assertThat(caseData.getUuid()).isOfAnyClassIn(UUID.randomUUID().getClass());
        assertThat(caseData.getCreated()).isOfAnyClassIn(LocalDateTime.now().getClass());
        assertThat(caseData.getCaseDataType()).isEqualTo(type);
        assertThat(caseData.getData()).isEqualTo("{}");
        assertThat(caseData.isPriority()).isEqualTo(false);
        assertThat(caseData.getPrimaryCorrespondentUUID()).isEqualTo(null);
        assertThat(caseData.getPrimaryTopicUUID()).isEqualTo(null);
        assertThat(caseData.getCaseDeadline()).isEqualTo(caseDeadline);

        Map<String, String> newData = new HashMap<>();
        newData.put("new", "anyValue");

        caseData.update(newData, objectMapper);

        assertThat(caseData.getData().contains("new")).isTrue();
        assertThat(caseData.getData().contains("anyValue")).isTrue();

        assertThat(caseData.getUuid()).isOfAnyClassIn(UUID.randomUUID().getClass());
        assertThat(caseData.getCreated()).isOfAnyClassIn(LocalDateTime.now().getClass());
        assertThat(caseData.getCaseDataType()).isEqualTo(type);
        assertThat(caseData.isPriority()).isEqualTo(false);
        assertThat(caseData.getPrimaryCorrespondentUUID()).isEqualTo(null);
        assertThat(caseData.getPrimaryTopicUUID()).isEqualTo(null);
        assertThat(caseData.getCaseDeadline()).isEqualTo(caseDeadline);
    }


    @Test
    public void updateWithExistingData() {

        CaseDataType type = CaseDataType.MIN;
        Long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();
        data.put("old", "anyOldValue");
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        ObjectMapper objectMapper = new ObjectMapper();

        CaseData caseData = new CaseData(type, caseNumber, data, objectMapper, caseDeadline);

        Map<String, String> newData = new HashMap<>();
        newData.put("new", "anyValue");

        caseData.update(newData, objectMapper);

        assertThat(caseData.getData().contains("old")).isTrue();
        assertThat(caseData.getData().contains("anyOldValue")).isTrue();
        assertThat(caseData.getData().contains("new")).isTrue();
        assertThat(caseData.getData().contains("anyValue")).isTrue();
    }

    @Test
    public void updateWithOverwriteExistingData() {

        CaseDataType type = CaseDataType.MIN;
        Long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();
        data.put("new", "anyOldValue");
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        ObjectMapper objectMapper = new ObjectMapper();

        CaseData caseData = new CaseData(type, caseNumber, data, objectMapper, caseDeadline);

        Map<String, String> newData = new HashMap<>();
        newData.put("new", "anyValue");

        caseData.update(newData, objectMapper);

        assertThat(caseData.getData().contains("new")).isTrue();
        assertThat(caseData.getData().contains("anyValue")).isTrue();
    }

    @Test
    public void updateWithEmptyData() {

        CaseDataType type = CaseDataType.MIN;
        Long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();
        data.put("new", "anyOldValue");
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        ObjectMapper objectMapper = new ObjectMapper();

        CaseData caseData = new CaseData(type, caseNumber, data, objectMapper, caseDeadline);

        Map<String, String> newData = new HashMap<>();

        caseData.update(newData, objectMapper);

        assertThat(caseData.getData().contains("new")).isTrue();
        assertThat(caseData.getData().contains("anyOldValue")).isTrue();
    }

    @Test
    public void updateWithNullData() {

        CaseDataType type = CaseDataType.MIN;
        Long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();
        data.put("new", "anyOldValue");
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        ObjectMapper objectMapper = new ObjectMapper();

        CaseData caseData = new CaseData(type, caseNumber, data, objectMapper, caseDeadline);

        caseData.update(null, objectMapper);

        assertThat(caseData.getData().contains("new")).isTrue();
        assertThat(caseData.getData().contains("anyOldValue")).isTrue();
    }

    @Test
    public void shouldUpdatePriority() {

        CaseDataType type = CaseDataType.MIN;
        Long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        CaseData caseData = new CaseData(type, caseNumber, data, objectMapper, caseDeadline);

        assertThat(caseData.getUuid()).isOfAnyClassIn(UUID.randomUUID().getClass());
        assertThat(caseData.getCreated()).isOfAnyClassIn(LocalDateTime.now().getClass());
        assertThat(caseData.getCaseDataType()).isEqualTo(type);
        assertThat(caseData.getData()).isEqualTo("{}");
        assertThat(caseData.isPriority()).isEqualTo(false);
        assertThat(caseData.getPrimaryCorrespondentUUID()).isEqualTo(null);
        assertThat(caseData.getPrimaryTopicUUID()).isEqualTo(null);
        assertThat(caseData.getCaseDeadline()).isEqualTo(caseDeadline);

        caseData.setPriority(true);

        assertThat(caseData.getUuid()).isOfAnyClassIn(UUID.randomUUID().getClass());
        assertThat(caseData.getCreated()).isOfAnyClassIn(LocalDateTime.now().getClass());
        assertThat(caseData.getCaseDataType()).isEqualTo(type);
        assertThat(caseData.getData()).isEqualTo("{}");
        assertThat(caseData.isPriority()).isEqualTo(true);
        assertThat(caseData.getPrimaryCorrespondentUUID()).isEqualTo(null);
        assertThat(caseData.getPrimaryTopicUUID()).isEqualTo(null);
        assertThat(caseData.getCaseDeadline()).isEqualTo(caseDeadline);
    }

    @Test
    public void shouldSetPrimaryCorrespondent() {

        CaseDataType type = CaseDataType.MIN;
        Long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        UUID primary = UUID.randomUUID();

        CaseData caseData = new CaseData(type, caseNumber, data, objectMapper, caseDeadline);

        assertThat(caseData.getUuid()).isOfAnyClassIn(UUID.randomUUID().getClass());
        assertThat(caseData.getCreated()).isOfAnyClassIn(LocalDateTime.now().getClass());
        assertThat(caseData.getCaseDataType()).isEqualTo(type);
        assertThat(caseData.getData()).isEqualTo("{}");
        assertThat(caseData.isPriority()).isEqualTo(false);
        assertThat(caseData.getPrimaryCorrespondentUUID()).isEqualTo(null);
        assertThat(caseData.getPrimaryTopicUUID()).isEqualTo(null);
        assertThat(caseData.getCaseDeadline()).isEqualTo(caseDeadline);

        caseData.setPrimaryCorrespondentUUID(primary);

        assertThat(caseData.getUuid()).isOfAnyClassIn(UUID.randomUUID().getClass());
        assertThat(caseData.getCreated()).isOfAnyClassIn(LocalDateTime.now().getClass());
        assertThat(caseData.getCaseDataType()).isEqualTo(type);
        assertThat(caseData.getData()).isEqualTo("{}");
        assertThat(caseData.isPriority()).isEqualTo(false);
        assertThat(caseData.getPrimaryCorrespondentUUID()).isEqualTo(primary);
        assertThat(caseData.getPrimaryTopicUUID()).isEqualTo(null);
        assertThat(caseData.getCaseDeadline()).isEqualTo(caseDeadline);

    }

    @Test
    public void updatePriority() {

        CaseDataType type = CaseDataType.MIN;
        Long caseNumber = 1234L;
        Map<String, String> data = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        LocalDate caseDeadline = LocalDate.now().plusDays(20);

        UUID primary = UUID.randomUUID();

        CaseData caseData = new CaseData(type, caseNumber, data, objectMapper, caseDeadline);

        assertThat(caseData.getUuid()).isOfAnyClassIn(UUID.randomUUID().getClass());
        assertThat(caseData.getCreated()).isOfAnyClassIn(LocalDateTime.now().getClass());
        assertThat(caseData.getCaseDataType()).isEqualTo(type);
        assertThat(caseData.getData()).isEqualTo("{}");
        assertThat(caseData.isPriority()).isEqualTo(false);
        assertThat(caseData.getPrimaryCorrespondentUUID()).isEqualTo(null);
        assertThat(caseData.getPrimaryTopicUUID()).isEqualTo(null);
        assertThat(caseData.getCaseDeadline()).isEqualTo(caseDeadline);

        caseData.setPrimaryTopicUUID(primary);

        assertThat(caseData.getUuid()).isOfAnyClassIn(UUID.randomUUID().getClass());
        assertThat(caseData.getCreated()).isOfAnyClassIn(LocalDateTime.now().getClass());
        assertThat(caseData.getCaseDataType()).isEqualTo(type);
        assertThat(caseData.getData()).isEqualTo("{}");
        assertThat(caseData.isPriority()).isEqualTo(false);
        assertThat(caseData.getPrimaryCorrespondentUUID()).isEqualTo(null);
        assertThat(caseData.getPrimaryTopicUUID()).isEqualTo(primary);
        assertThat(caseData.getCaseDeadline()).isEqualTo(caseDeadline);

    }


}