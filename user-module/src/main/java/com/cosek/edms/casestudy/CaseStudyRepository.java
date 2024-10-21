package com.cosek.edms.casestudy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseStudyRepository extends JpaRepository<CaseStudy, Long> {
    @Query(value = "SELECT user_id FROM case_study_user WHERE case_study_id = :caseStudyId", nativeQuery = true)
    List<Long> findAssignedUserIdsByCaseStudyId(@Param("caseStudyId") Long caseStudyId);

    @Query(value = "SELECT description,name FROM case_study WHERE id = :caseStudyId", nativeQuery = true)
    CaseStudy findCaseStudyById(@Param("caseStudyId") Long caseStudyId);
}
