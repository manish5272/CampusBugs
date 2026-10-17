package com.campusbugs.campusbugs.controller;


import com.campusbugs.campusbugs.dto.IssueRequest;
import com.campusbugs.campusbugs.dto.IssueResponse;
import com.campusbugs.campusbugs.model.Category;
import com.campusbugs.campusbugs.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    public final IssueService issueService;

    public IssueController(IssueService issueService){
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponse> create(@Valid @RequestBody IssueRequest request, Authentication authentication){
        IssueResponse created = issueService.create(request,authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }

    @GetMapping
    public List<IssueResponse> list(@RequestParam(required = false) Category category){
        return issueService.list(category);
    }

    @PostMapping("/{id}/upvote")
    public IssueResponse upvote(@PathVariable Long id, Authentication authentication){
        return issueService.upvote(id, authentication.getName());
    }



}
