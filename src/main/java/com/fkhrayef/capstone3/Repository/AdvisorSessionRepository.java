package com.fkhrayef.capstone3.Repository;

import com.fkhrayef.capstone3.Model.AdvisorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvisorSessionRepository extends JpaRepository<AdvisorSession,Integer> {
    List<AdvisorSession> findAdvisorSessionByStartupId(Integer startupId);

    AdvisorSession findAdvisorSessionByStartupIdAndStartDateAndNotes(Integer startupId, LocalDateTime startDate, String notes);

    AdvisorSession findAdvisorSessionById(Integer id);

    @Query("select s from AdvisorSession s where s.id=:sessionId and s.status=:status")
    boolean giveMeByAdvisorSessionIdAndStatus(Integer sessionId,String status);
}
