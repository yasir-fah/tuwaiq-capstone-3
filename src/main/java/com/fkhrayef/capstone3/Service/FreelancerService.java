package com.fkhrayef.capstone3.Service;

import com.fkhrayef.capstone3.Api.ApiException;
import com.fkhrayef.capstone3.Model.Freelancer;
import com.fkhrayef.capstone3.Repository.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreelancerService {
    private final FreelancerRepository freelancerRepository;

    public List<Freelancer> getAllFreelancers(){
        return freelancerRepository.findAll();
    }

    public void addFreelancer(Freelancer freelancer){
        freelancerRepository.save(freelancer);
    }

    public void updateFreelancer(Integer id,Freelancer freelancer) {

        // 1- check if freelancer exist:
        Freelancer oldFreelancer = freelancerRepository.findFreelancerById(id);
        if (oldFreelancer == null) {
            throw new ApiException("freelancer not exist");
        }

        // 2- apply changes;
        oldFreelancer.setName(freelancer.getName());
        oldFreelancer.setEmail(freelancer.getEmail());
        oldFreelancer.setPhone(freelancer.getPhone());
        oldFreelancer.setSpecialization(freelancer.getSpecialization());
        oldFreelancer.setHourlyRate(freelancer.getHourlyRate());
        oldFreelancer.setIsAvailable(freelancer.getIsAvailable());
        oldFreelancer.setYearsExperience(freelancer.getYearsExperience());
        oldFreelancer.setRating(freelancer.getRating());

        // 3- save changes:
        freelancerRepository.save(oldFreelancer);
    }

    public void deleteFreelancer(Integer id){

        // 1- check if freelancer exist:
        Freelancer oldFreelancer = freelancerRepository.findFreelancerById(id);
        if (oldFreelancer == null) {
            throw new ApiException("freelancer not exist");
        }

        // 2- delete freelancer:
        freelancerRepository.delete(oldFreelancer);
    }
}
