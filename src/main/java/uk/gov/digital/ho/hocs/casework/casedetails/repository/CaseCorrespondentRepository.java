package uk.gov.digital.ho.hocs.casework.casedetails.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.digital.ho.hocs.casework.casedetails.model.CaseCorrespondent;

@Repository
public interface CaseCorrespondentRepository extends CrudRepository<CaseCorrespondent, String> {
}
