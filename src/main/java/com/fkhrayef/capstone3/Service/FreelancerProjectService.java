package com.fkhrayef.capstone3.Service;

import com.fkhrayef.capstone3.Api.ApiException;
import com.fkhrayef.capstone3.DTOin.FreelancerProjectDTO;
import com.fkhrayef.capstone3.Model.Freelancer;
import com.fkhrayef.capstone3.Model.FreelancerProject;
import com.fkhrayef.capstone3.Model.Startup;
import com.fkhrayef.capstone3.Repository.FreelancerProjectRepository;
import com.fkhrayef.capstone3.Repository.FreelancerRepository;
import com.fkhrayef.capstone3.Repository.StartupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreelancerProjectService {

    private final StartupRepository startupRepository;
    private final FreelancerProjectRepository freelancerProjectRepository;
    private final FreelancerRepository freelancerRepository;

    // list of freelancerProject from certain startup:
    public List<FreelancerProject> getAllFreelancerProject(Integer startupId){

        // 1- find the desired startup:
        Startup startup = startupRepository.findStartupById(startupId);
        if(startup == null){
            throw new ApiException("startup not found");
        }

        // 2- fetch the freelancerProjects from desired startup:
        List<FreelancerProject> freelancerProjects = freelancerProjectRepository.giveMeFreelancerProjectByStartupId(startupId);
        if(freelancerProjects.isEmpty()){
            throw new ApiException("this startup has no freelancer projects");
        }

        return freelancerProjects;
    }


    /// add project by startup:
    public void addFreelancerProjectByStartup(Integer startupId, FreelancerProjectDTO freelancerProjectDTO){

        //1- check if startup exist:
        Startup startup = startupRepository.findStartupById(startupId);
        if(startup == null){
            throw new ApiException("startup not found");
        }

        // 2- prevent startup from have multiple project with same name & description:
        FreelancerProject project = freelancerProjectRepository.
                findFreelancerProjectByProjectNameAndDescription
                        (freelancerProjectDTO.getProjectName(),freelancerProjectDTO.getDescription());
        if(project != null){
            throw new ApiException("there is similar project");
        }

        // 3- add values for the project:
        FreelancerProject freelancerProject = new FreelancerProject();
        freelancerProject.setProjectName(freelancerProjectDTO.getProjectName());
        freelancerProject.setDescription(freelancerProjectDTO.getDescription());
        freelancerProject.setStartDate(freelancerProjectDTO.getStartDate());
        freelancerProject.setEndDate(freelancerProjectDTO.getEndDate());
        freelancerProject.setStatus("active"); // for now it's active.

        // 4- link the project with the startup & save:
        freelancerProject.setStartup(startup);
        freelancerProjectRepository.save(freelancerProject);

    }


    /// assign project of some startup to freelancer
    public void assignFreelancerProjectToFreelancer(Integer projectId, Integer startupId, Integer freelancerId){

        // 1- check if project exist;
        FreelancerProject project = freelancerProjectRepository.findFreelancerProjectById(projectId);
        if(project == null){
            throw new ApiException("project not found");
        }

        // 2- check if startup exist:
        Startup startup = startupRepository.findStartupById(startupId);
        if(startup == null){
            throw new ApiException("startup not found");
        }

        // 3- check if freelancer exist:
        Freelancer freelancer = freelancerRepository.findFreelancerById(freelancerId);
        if(freelancer == null){
            throw new ApiException("freelancer not found");
        }

        // 4- check if startup own the project:
        if(project.getStartup() == null || !project.getStartup().getId().equals(startup.getId())){
            throw new ApiException("project and startup not belong to each other");
        }

        // 5- check availability of freelancer:
        if(!freelancer.getIsAvailable()){
            throw new ApiException("freelancer not available now");
        }


        project.setFreelancer(freelancer);
        freelancerProjectRepository.save(project);
    }



    public void updateFreelancerProject(Integer projectId, Integer startupId, FreelancerProjectDTO dto) {

        // 1- check project exists
        FreelancerProject project = freelancerProjectRepository.findFreelancerProjectById(projectId);
        if (project == null) {
            throw new ApiException("project not found");
        }

        // 2- check startup exists
        Startup startup = startupRepository.findStartupById(startupId);
        if (startup == null) {
            throw new ApiException("startup not found");
        }

        // 3- check ownership (avoid NPE)
        if (project.getStartup() == null || !project.getStartup().getId().equals(startup.getId())) {
            throw new ApiException("project and startup not belong to each other");
        }

        // 4- validate dates
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new ApiException("start date must be before or equal to end date");
        }

        // 5- prevent duplicate name+description within the same startup (excluding this project)
        FreelancerProject dup = freelancerProjectRepository
                .findFreelancerProjectByProjectNameAndDescription(dto.getProjectName(), dto.getDescription());
        if (dup != null
                && dup.getId() != null
                && !dup.getId().equals(project.getId())
                && dup.getStartup() != null
                && dup.getStartup().getId().equals(startup.getId())) {
            throw new ApiException("there is similar project for this startup");
        }

        // 6- apply updates (status unchanged here)
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());

        // 7- save
        freelancerProjectRepository.save(project);
    }



    ///  delete project with in both sides (startup & freelancer)
    public void deleteFreelancerProject(Integer projectId, Integer startupId){

        // 1-  check project exists
        FreelancerProject project = freelancerProjectRepository.findFreelancerProjectById(projectId);
        if (project == null) {
            throw new ApiException("project not found");
        }

        // 2- check from startup:
        Startup startup = startupRepository.findStartupById(startupId);
        if (startup == null) {
            throw new ApiException("startup not found");
        }

        // 3- check ownership
        if (project.getStartup() == null || !project.getStartup().getId().equals(startup.getId())) {
            throw new ApiException("project and startup not belong to each other");
        }

        // 4- break relations
        project.setFreelancer(null);
        project.setStartup(null);

        // 5- delete project
        freelancerProjectRepository.delete(project);

    }






}
