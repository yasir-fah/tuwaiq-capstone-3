package com.fkhrayef.capstone3.Repository;

import com.fkhrayef.capstone3.Model.FreelancerProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreelancerProjectRepository extends JpaRepository<FreelancerProject,Integer> {
}
