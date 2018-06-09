package uk.gov.digital.ho.hocs.casework.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
class AuditResource {

    private final AuditService auditService;

    @Autowired
    public AuditResource(AuditService auditService) {

        this.auditService = auditService;
    }


    @RequestMapping(value = "/rsh/report/current", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
    public ResponseEntity<String> rshReportCurrent(@RequestHeader("X-Auth-Username") String username) {
        String value = auditService.extractData(new String[]{"RSH"}, LocalDate.now(), username);
        return ResponseEntity.ok(value);
    }

        @RequestMapping(value = "/rsh/report/{cutoff}", method = RequestMethod.GET, produces = "text/csv;charset=UTF-8")
    public ResponseEntity<String> rshReportCutoff(@PathVariable("cutoff") String cutoff, @RequestHeader("X-Auth-Username") String username) {
        LocalDate cutoffDate = LocalDate.parse(cutoff);
        String value = auditService.extractData(new String[]{"RSH"}, cutoffDate, username);
        return ResponseEntity.ok(value);
    }
}
