package com.fkhrayef.capstone3.Repository;

import com.fkhrayef.capstone3.Model.AdvisorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvisorSessionRepository extends JpaRepository<AdvisorSession, Integer> {
    
    AdvisorSession findAdvisorSessionById(Integer id);
}
