package com.campusbugs.campusbugs.model;

import jakarta.persistence.*;


@Entity
@Table(name="reports",uniqueConstraints = @UniqueConstraint(columnNames = {"issue_id","report_hash"}))
public class Report {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "issue_id", nullable = false)
    private long issueId;

    @Column(name = "reporter_hash", nullable = false)
    private String reporterHash;

    //auto get and set


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIssueId() {
        return issueId;
    }

    public void setIssueId(long issueId) {
        this.issueId = issueId;
    }

    public String getReporterHash() {
        return reporterHash;
    }

    public void setReporterHash(String reporterHash) {
        this.reporterHash = reporterHash;
    }
}




