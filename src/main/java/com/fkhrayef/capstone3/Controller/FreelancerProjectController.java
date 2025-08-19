package com.fkhrayef.capstone3.Controller;

import com.fkhrayef.capstone3.Api.ApiResponse;
import com.fkhrayef.capstone3.DTOin.FreelancerProjectDTO;
import com.fkhrayef.capstone3.Service.FreelancerProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/freelancer-project")
@RequiredArgsConstructor
public class FreelancerProjectController {
    private final FreelancerProjectService freelancerProjectService;

    @GetMapping("/get/startup/project/{startup_id}")
    public ResponseEntity<?> getAllFreelancerProject(@PathVariable Integer startup_id){
        return ResponseEntity.status(200).body(freelancerProjectService.getAllFreelancerProject(startup_id));
    }

    @PostMapping("/add/project/to/{startup_id}")
    public ResponseEntity<?> addFreelancerProjectByStartup(@PathVariable Integer startup_id, @Valid @RequestBody FreelancerProjectDTO projectDTO){
        freelancerProjectService.addFreelancerProjectByStartup(startup_id,projectDTO);
        return ResponseEntity.status(200).body(new ApiResponse("project added to startup successfully"));
    }


    @PutMapping("/update/assign/{project_id}/by/{startup_id}/to/{freelance_id}")
    public ResponseEntity<?> assignFreelancerProjectToFreelancer(@PathVariable Integer project_id,
                                                                 @PathVariable Integer startup_id,
                                                                 @PathVariable Integer freelance_id){
        freelancerProjectService.assignFreelancerProjectToFreelancer(project_id,startup_id,freelance_id);
        return ResponseEntity.status(200).body(new ApiResponse("freelancer assigned to project successfully"));
    }

    @PutMapping("/update/{project_id}/by/{startup_id}")
    public ResponseEntity<?> updateFreelancerProject(@PathVariable("project_id") Integer projectId,
                                                     @PathVariable("startup_id") Integer startupId,
                                                     @RequestBody @Valid FreelancerProjectDTO freelancerProjectDTO) {
        freelancerProjectService.updateFreelancerProject(projectId, startupId, freelancerProjectDTO);
        return ResponseEntity.status(200).body(new ApiResponse("project updated successfully"));
    }


    @DeleteMapping("/delete/{project_id}/by/{startup_id}")
    public ResponseEntity<?> deleteFreelancerProject(@PathVariable("project_id") Integer projectId,
                                                     @PathVariable("startup_id") Integer startupId) {
        freelancerProjectService.deleteFreelancerProject(projectId, startupId);
        return ResponseEntity.status(200).body(new ApiResponse("project deleted successfully"));
    }


}
