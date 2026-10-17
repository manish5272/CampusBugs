package com.campusbugs.campusbugs.service;


import com.campusbugs.campusbugs.dto.IssueRequest;
import com.campusbugs.campusbugs.dto.IssueResponse;
import com.campusbugs.campusbugs.model.Category;
import com.campusbugs.campusbugs.model.Issue;
import com.campusbugs.campusbugs.model.Report;
import com.campusbugs.campusbugs.repository.IssueRepository;
import com.campusbugs.campusbugs.repository.ReportRepository;
import com.campusbugs.campusbugs.repository.VoteRepository;
import com.campusbugs.campusbugs.model.Status;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



import com.campusbugs.campusbugs.model.Vote;

import org.springframework.dao.DataIntegrityViolationException;




@Service
public class IssueService {


    private static final int MAX_POSTS_PER_DAY = 3;
    private static final int REPORTS_TO_HIDE = 3;




    private final IssueRepository issueRepository ;
    private final VoteRepository voteRepository;
    private final AnonymizerService anonymizer;
    private final ReportRepository reportRepository;
    private final ContentFilterService contentFilter;


    public IssueService(IssueRepository issueRepository, VoteRepository voteRepository,ReportRepository reportRepository,AnonymizerService anonymizer,ContentFilterService contentFilter)
    {
        this.issueRepository = issueRepository;
        this.voteRepository = voteRepository;
        this.reportRepository = reportRepository;
        this.anonymizer= anonymizer;
        this.contentFilter = contentFilter;

    }


    //-------------------STUDENT RELATED STUFF------------------

    public IssueResponse create(IssueRequest request,String rollNumber){


        String title = request.title().trim();
        String description = request.description().trim();


        if(contentFilter.containBlockedWords(title)
                || contentFilter.containBlockedWords(description))

        {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"Please keep your language clean");

        }

        String authHash = authorHashOf(rollNumber);
        LocalDateTime since = LocalDateTime.now().minusHours(24);

        if(issueRepository.countByAuthorHashAndCreatedAtAfter(authHash,since) >= MAX_POSTS_PER_DAY)
        {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS," You can created upto 3 posts in 24 hours");

        }






        Issue issue = new Issue();
        issue.setTitle(request.title().trim());
        issue.setDescription(request.description().trim());
        issue.setCategory(request.category());

        issue.setAuthorHash(authHash);


        return toResponse(issueRepository.save(issue));

    }

    public List<IssueResponse> list(Category category){

        List<Issue> issues;
        if (category == null)
        {
            issues = issueRepository.findByHiddenFalseOrderByUpvoteCountDesc();

        }
        else {
            issues = issueRepository.findByCategoryAndHiddenFalseOrderByUpvoteCountDesc(category);
        }

        return toResponses(issues);

}

    @Transactional
    public IssueResponse upvote(Long issueId,String rollNumber) {
        Issue issue = findVisibleIssue(issueId);

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



    @Transactional
    public void report(Long issueId,String rollNumber) {
        Issue issue = findVisibleIssue(issueId);

        if(issue.getAuthorHash().equals(authorHashOf(rollNumber)))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You cannot report your own post");
        }

        String reporterHash = anonymizer.hash("report:" + issueId + ":" + rollNumber);

        if(reportRepository.existsByIssueIdAndReporterHash(issueId,reporterHash))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"You already reported this post");
        }

        Report report = new Report();
        report.setIssueId((issueId));
        report.setReporterHash(reporterHash);

        try{
            reportRepository.save(report);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"You already reported this issue");
        }

        issue.setReportCount(issue.getReportCount()+1);
        if(issue.getReportCount() >= REPORTS_TO_HIDE)
        {
            issue.setHidden(true);
        }
        issueRepository.save(issue);

    }



//-----------------------------ADMIN FEATURES STUFF ---------------------------

    @Transactional
    public IssueResponse updateStatus(Long issueId, Status newStatus) {
        Issue issue = findIssue(issueId);

        if(issue.getStatus() == newStatus){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Issue is already" + newStatus);
        }

        issue.setStatus(newStatus);
        return toResponse(issueRepository.save(issue));
    }


    public List<IssueResponse> listHidden(){
        return toResponses(issueRepository.findByHiddenTrueOrderByCreatedAtDesc());
    }

    @Transactional
    public IssueResponse restore(Long issueId){
        Issue issue = findIssue(issueId);
        issue.setHidden(false);
        issue.setReportCount(0);
        return toResponse(issueRepository.save(issue));
    }


//-----------------------HELPERS ------------------------------



    private Issue findIssue(Long issueId){
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Issue not found"));
    }

    private Issue findVisibleIssue(Long issueId) {
        Issue issue = findIssue(issueId);
        if (issue.isHidden()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Issue not found");
        }
        return issue;
    }

//-----------------------OLD HELPERS----------------------------

    private String authorHashOf(String rollNumber) {
        return anonymizer.hash("author:" + rollNumber);

    }

    private String voterHashOf(Long issueId, String rollNumber){
        return anonymizer.hash("vote:" + issueId + ":" + rollNumber);

    }







    private List<IssueResponse> toResponses(List<Issue> issues){
        List<IssueResponse> responses = new ArrayList<>();
        for(Issue issue : issues){
            responses.add(toResponse(issue));
        }
        return responses;
    }

    private IssueResponse toResponse(Issue issue) {
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





