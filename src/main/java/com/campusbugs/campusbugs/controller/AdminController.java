package com.campusbugs.campusbugs.controller;


import com.campusbugs.campusbugs.dto.IssueResponse;
import com.campusbugs.campusbugs.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;



import com.campusbugs.campusbugs.dto.StatusRequest;


import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final IssueService issueService;

    public AdminController(IssueService issueService){
        this.issueService = issueService;
    }


    @PatchMapping("/issues/{id}/status")
    public IssueResponse updateStatus(@PathVariable Long id,
                                      @Valid @RequestBody StatusRequest request){


        return issueService.updateStatus(id,request.status());


    }

    @GetMapping("/issues/hidden")
    public List<IssueResponse> hidden(){
        return issueService.listHidden();
    }

    @PostMapping("/issues/{id}/restore")
    public IssueResponse restore(@PathVariable Long id){
        return issueService.restore(id);
    }
}
