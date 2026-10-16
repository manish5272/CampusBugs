package com.campusbugs.campusbugs.service;


import com.campusbugs.campusbugs.dto.IssueRequest;
import com.campusbugs.campusbugs.dto.IssueResponse;
import com.campusbugs.campusbugs.model.Category;
import com.campusbugs.campusbugs.model.Issue;
import com.campusbugs.campusbugs.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;








@Service
public class IssueService {

    private final IssueRepository issueRepository ;

    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public IssueResponse create(IssueRequest request){

        Issue issue = new Issue();
        issue.setTitle(request.title().trim());
        issue.setDescription(request.description().trim());
        issue.setCategory(request.category());
        issue.setAuthorHash("TEMP");

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


