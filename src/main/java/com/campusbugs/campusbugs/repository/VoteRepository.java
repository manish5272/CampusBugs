package com.campusbugs.campusbugs.repository;

import com.campusbugs.campusbugs.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByIssueIdAndVoterHash(Long issueId, String voterHash);
}
