package com.fkhrayef.capstone3.Service;

import com.fkhrayef.capstone3.Api.ApiException;
import com.fkhrayef.capstone3.DTOin.FreelancerDTO;
import com.fkhrayef.capstone3.Model.Freelancer;
import com.fkhrayef.capstone3.Model.FreelancerProject;
import com.fkhrayef.capstone3.Repository.FreelancerProjectRepository;
import com.fkhrayef.capstone3.Repository.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreelancerService {
    private final FreelancerRepository freelancerRepository;
    private final FreelancerProjectRepository freelancerProjectRepository;

    public List<Freelancer> getAllFreelancers() {
        return freelancerRepository.findAll();
    }

    public void addFreelancer(FreelancerDTO dto) {
        Freelancer freelancer = new Freelancer(null,dto.getName(), dto.getEmail(),dto.getPhone(),dto.getSpecialization(),dto.getHourlyRate(),true,dto.getYearsExperience(),null,null,null);
        freelancerRepository.save(freelancer);
    }

    public void updateFreelancer(Integer id, FreelancerDTO dto) {

        // 1- check if freelancer exist:
        Freelancer oldFreelancer = freelancerRepository.findFreelancerById(id);
        if (oldFreelancer == null) {
            throw new ApiException("freelancer not exist");
        }

        // 2- apply changes;
        oldFreelancer.setName(dto.getName());
        oldFreelancer.setEmail(dto.getEmail());
        oldFreelancer.setPhone(dto.getPhone());
        oldFreelancer.setSpecialization(dto.getSpecialization());
        oldFreelancer.setHourlyRate(dto.getHourlyRate());
        oldFreelancer.setYearsExperience(dto.getYearsExperience());

        // 3- save changes:
        freelancerRepository.save(oldFreelancer);
    }

    public void deleteFreelancer(Integer id) {

        // 1- check if freelancer exist:
        Freelancer oldFreelancer = freelancerRepository.findFreelancerById(id);
        if (oldFreelancer == null) {
            throw new ApiException("freelancer not exist");
        }

        // 2- delete freelancer:
        freelancerRepository.delete(oldFreelancer);
    }

    // extra endpoint: create endpoint that takes freelancer_id & status, then return list of project depend on status he chose:
    public List<FreelancerProject> getAllFreelancerProjectByStatus(Integer freelanceId, String status) {

        // 1- check if freelance exist:
        Freelancer freelancer = freelancerRepository.findFreelancerById(freelanceId);
        if (freelancer == null) {
            throw new ApiException("freelancer not found");
        }

        // 3- validate from status input:
        if(!status.equals("pending")
                && !status.equals("cancelled")
                && !status.equals("accepted")
                && !status.equals("rejected")
                && !status.equals("active")
                && !status.equals("completed")){
            throw new ApiException("status should be active|completed|pending|rejected|accepted|cancelled");
        }

        // 2- grab freelancer with desired attribute:
        List<FreelancerProject> freelancerProjects =
                freelancerProjectRepository.
                        giveMeFreelancerProjectByFreelancerIdAndStatus
                        (freelancer.getId(), status);

        return freelancerProjects;
    }
}
