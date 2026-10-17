package com.campusbugs.campusbugs.service;


import com.campusbugs.campusbugs.dto.IssueRequest;
import com.campusbugs.campusbugs.dto.IssueResponse;
import com.campusbugs.campusbugs.model.Category;
import com.campusbugs.campusbugs.model.Issue;
import com.campusbugs.campusbugs.repository.IssueRepository;
import com.campusbugs.campusbugs.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;



import com.campusbugs.campusbugs.model.Vote;

import org.springframework.dao.DataIntegrityViolationException;




@Service
public class IssueService {

    private final IssueRepository issueRepository ;
    private final VoteRepository voteRepository;
    private final AnonymizerService anonymizer;


    public IssueService(IssueRepository issueRepository, VoteRepository voteRepository, AnonymizerService anonymizer)
    {
        this.issueRepository = issueRepository;
        this.voteRepository = voteRepository;
        this.anonymizer= anonymizer;
    }

    public IssueResponse create(IssueRequest request,String rollNumber){

        Issue issue = new Issue();
        issue.setTitle(request.title().trim());
        issue.setDescription(request.description().trim());
        issue.setCategory(request.category());

        issue.setAuthorHash(authorHashOf(rollNumber));

        Issue saved = issueRepository.save(issue);
        return toResponse(saved);

    }

    public List<IssueResponse> list(Category category){

        List<Issue> issues;
        if (category == null) {
            issues = issueRepository.findAllByOrderByUpvoteCountDesc();

        }
        else {
            issues = issueRepository.findByCategoryOrderByUpvoteCountDesc(category);
    }


        List<IssueResponse> responses = new ArrayList<>();
        for(Issue issue : issues) {
            responses.add(toResponse(issue));
    }

        return responses;

}

    @Transactional
    public IssueResponse upvote(Long issueId,String rollNumber) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Issue not found")
                );

        if (issue.getAuthorHash().equals(authorHashOf(rollNumber))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You cannot upvote your own issue"
            );
        }

        String voterHash = voterHashOf(issueId, rollNumber);

        if (voteRepository.existsByIssueIdAndVoterHash(issueId, voterHash)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "You already upvoted this issue");

        }

        Vote vote = new Vote();
        vote.setIssueId(issueId);
        vote.setVoterHash(voterHash);

        try {
            voteRepository.save(vote);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "You already upvoted this issue");

        }

        issue.setUpvoteCount(issue.getUpvoteCount() + 1);
        issueRepository.save(issue);
        return toResponse(issue);





    }

    private String authorHashOf(String rollNumber) {
        return anonymizer.hash("author:" + rollNumber);

    }

    private String voterHashOf(Long issueId, String rollNumber){
        return anonymizer.hash("vote:" + issueId + ":" + rollNumber);

    }







    private IssueResponse toResponse(Issue issue){
        return new IssueResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getCategory(),
                issue.getStatus(),
                issue.getUpvoteCount(),
                issue.getCreatedAt());
    }


}


