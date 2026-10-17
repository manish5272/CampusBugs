package com.campusbugs.campusbugs.repository;

import com.campusbugs.campusbugs.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {


    boolean existsByIssueIdAndReporterHash(long issueId,String reporterHash);
}
