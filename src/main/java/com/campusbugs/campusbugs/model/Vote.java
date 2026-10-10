package com.campusbugs.campusbugs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;



    @Entity
    @Table(name = "votes",
            uniqueConstraints = @UniqueConstraint(columnNames = {"issue_id", "voter_hash"}))
    public class Vote{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(name = "issue_id",nullable = false)
        private long issueId;

        @Column(name = "voter_hash" , nullable = false)
        private String voterHash;


        //auto get and set

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getVoterHash() {
            return voterHash;
        }

        public void setVoterHash(String voterHash) {
            this.voterHash = voterHash;
        }

        public long getIssueId() {
            return issueId;
        }

        public void setIssueId(long issueId) {
            this.issueId = issueId;
        }
    }

