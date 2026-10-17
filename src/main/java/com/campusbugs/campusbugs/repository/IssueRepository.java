package com.campusbugs.campusbugs.repository;

import com.campusbugs.campusbugs.model.Category;
import com.campusbugs.campusbugs.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByHiddenFalseOrderByUpvoteCountDesc();

    List<Issue> findByCategoryAndHiddenFalseOrderByUpvoteCountDesc(Category category);

    List<Issue> findByHiddenTrueOrderByCreatedAtDesc();


    long countByAuthorHashAndCreatedAtAfter(String authorHash, LocalDateTime since);
}
