package com.fkhrayef.capstone3.Repository;

import com.fkhrayef.capstone3.Model.FreelancerProject;
import com.fkhrayef.capstone3.Model.Startup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreelancerProjectRepository extends JpaRepository<FreelancerProject,Integer> {

    @Query("select p from FreelancerProject p where p.startup.id=:startupId")
    List<FreelancerProject> giveMeFreelancerProjectByStartupId(Integer startupId);

    FreelancerProject findFreelancerProjectByProjectNameAndDescription(String projectName, String description);

    FreelancerProject findFreelancerProjectById(Integer id);

    Boolean getFreelancerProjectByStartupIs(Startup startup);
}
