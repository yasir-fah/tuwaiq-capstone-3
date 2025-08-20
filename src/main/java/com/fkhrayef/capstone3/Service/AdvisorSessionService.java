package com.fkhrayef.capstone3.Service;

import com.fkhrayef.capstone3.Api.ApiException;
import com.fkhrayef.capstone3.DTOin.AdvisorSessionDTO;
import com.fkhrayef.capstone3.Model.Advisor;
import com.fkhrayef.capstone3.Model.AdvisorSession;
import com.fkhrayef.capstone3.Model.Startup;
import com.fkhrayef.capstone3.Repository.AdvisorRepository;
import com.fkhrayef.capstone3.Repository.AdvisorSessionRepository;
import com.fkhrayef.capstone3.Repository.StartupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvisorSessionService {

    private final AdvisorSessionRepository advisorSessionRepository;
    private final StartupRepository startupRepository;
    private final AdvisorRepository advisorRepository;

    ///  1- get sessions of one startup
    public List<AdvisorSession> getAllAdvisorSessionsFromStartup(Integer startupId){

       // 1- check if startup exist:
       Startup startup = startupRepository.findStartupById(startupId);
       if(startup == null) {
           throw new ApiException("startup not found");
       }
       return advisorSessionRepository.findAdvisorSessionByStartupId(startup.getId());
   }


   /// 2- startup add new session
   public void addAdvisorSessionByStartup(Integer startupId, AdvisorSessionDTO dto){

        // 1- check if startup exist:
       Startup startup = startupRepository.findStartupById(startupId);
       if(startup == null){
           throw new ApiException("startup not found");
       }

       //  2- prevent startup from add duplicate sessions
       AdvisorSession session =
               advisorSessionRepository.
                       findAdvisorSessionByStartupIdAndStartDateAndNotes
                               (startup.getId(),dto.getStartDate(),dto.getNotes());
       if(session != null){
           throw new ApiException("this session already exist");
       }

       // 3- add the request values:
       AdvisorSession advisorSession = new AdvisorSession();
       advisorSession.setStartDate(dto.getStartDate());
       advisorSession.setDuration_minutes(dto.getDuration_minutes());
       advisorSession.setNotes(dto.getNotes());
       advisorSession.setStatus("pending");

       // 4- link session with startup & save the session:
       advisorSession.setStartup(startup);
       advisorSessionRepository.save(advisorSession);
   }


   ///  3- startup assign session to an advisor:
   public void assignAdvisorSessionToAdvisor(Integer sessionId, Integer startupId, Integer advisorId){

       // 1- check if session exist:
       AdvisorSession session = advisorSessionRepository.findAdvisorSessionById(sessionId);
       if(session == null){
           throw new ApiException("session not found");
       }

       // 2- check if startup exist:
       Startup startup = startupRepository.findStartupById(startupId);
       if(startup == null){
           throw new ApiException("startup not found");
       }

       // 3- check if advisor exist:
       Advisor advisor = advisorRepository.findAdvisorById(advisorId);
       if(advisor == null){
           throw new ApiException("advisor not found");
       }

       // 4- check if startup own this session:
       if(session.getStartup().getId() == null || !session.getStartup().getId().equals(startup.getId())){
           throw new ApiException("session does not have startup or session & startup not belong to each other");
       }

       // 5- check advisor availability:
       if(!advisor.getIsAvailable()){
           throw new ApiException("advisor not available...");
       }

       // 6- check from session status:
       if(!session.getStatus().equals("pending")){
           throw new ApiException("can't assign non-pending session");
       }

       // 7- link & save
       session.setAdvisor(advisor);
       advisorSessionRepository.save(session);
   }


   /// 4- advisor accepts session:
    public void advisorAcceptAdvisorSession(Integer advisorId, Integer sessionId){

        // 1- check if advisor exist:
        Advisor advisor = advisorRepository.findAdvisorById(advisorId);
        if(advisor == null){
            throw new ApiException("advisor not found");
        }

        // 2- check if session exist:
        AdvisorSession session = advisorSessionRepository.findAdvisorSessionById(sessionId);
        if(session == null){
            throw new ApiException("session not found");
        }

        // 3- check if session and advisor related:
        if(session.getAdvisor().getId() == null || !session.getAdvisor().getId().equals(advisor.getId())){
            throw new ApiException("session & advisor not belong to each other");
        }

        // 4- check from session status:
        if(!session.getStatus().equals("pending")){
            throw new ApiException("session status must be pending");
        }

        // change session status:
        session.setStatus("scheduled"); // scheduled successfully
        advisorSessionRepository.save(session);
    }


    /// 5- advisor reject session:
    public void advisorRejectAdvisorSession(Integer advisorId, Integer sessionId){

        // 1- check if advisor exist:
        Advisor advisor = advisorRepository.findAdvisorById(advisorId);
        if(advisor == null){
            throw new ApiException("advisor not found");
        }

        // 2- check if session exist:
        AdvisorSession session = advisorSessionRepository.findAdvisorSessionById(sessionId);
        if(session == null){
            throw new ApiException("session not found");
        }

        // 3- check if session and advisor related:
        if(session.getAdvisor().getId() == null || !session.getAdvisor().getId().equals(advisor.getId())){
            throw new ApiException("session & advisor not belong to each other");
        }

        // 4- check from session status:
        if(!session.getStatus().equals("pending")){
            throw new ApiException("session status must be pending");
        }

        // change session status:
        session.setStatus("rejected"); // rejected successfully
        advisorSessionRepository.save(session);
    }


    /// 6- allow startup to cancel their request:
    public void startupCancelAdvisorRequest(Integer startupId, Integer sessionId, Integer advisorId){

        // 1- check if startup exist:
        Startup startup = startupRepository.findStartupById(startupId);
        if(startup == null){
            throw new ApiException("startup not found");
        }

        // 2- check if session exist:
        AdvisorSession session = advisorSessionRepository.findAdvisorSessionById(sessionId);
        if(session == null){
            throw new ApiException("session not found");
        }

        // 3- check if advisor exist:
        Advisor advisor = advisorRepository.findAdvisorById(advisorId);
        if(advisor == null){
            throw new ApiException("advisor not found");
        }

        // 4- check session belong to same startup & advisor:
        if(session.getStartup().getId() == null
                || session.getAdvisor().getId() == null
                || !session.getStartup().getId().equals(startup.getId())
                || !session.getAdvisor().getId().equals(advisor.getId())
        ){
            throw new ApiException("advisor or startup not belong to project");
        }

        // 5- check if status of session still pending:
        if(!session.getAdvisor().equals("pending")){
            throw new ApiException("status should be pending to cancel advising session");
        }

        session.setStatus("cancelled");
        advisorSessionRepository.save(session);
    }

    ///  7- update advisor session;
    public void updateAdvisorSession(Integer sessionId, Integer startupId, AdvisorSessionDTO dto) {

        // 1- check session exists
        AdvisorSession session = advisorSessionRepository.findAdvisorSessionById(sessionId);
        if (session == null) {
            throw new ApiException("session not found");
        }

        // 2- check startup exists
        Startup startup = startupRepository.findStartupById(startupId);
        if (startup == null) {
            throw new ApiException("startup not found");
        }

        // 3- ownership
        if (session.getStartup() == null || !session.getStartup().getId().equals(startup.getId())) {
            throw new ApiException("session and startup not belong to each other");
        }

        // 4- only allow editing when pending
        if (!"pending".equals(session.getStatus())) {
            throw new ApiException("only pending sessions can be updated");
        }

        // 5- prevent duplicate
        AdvisorSession dup = advisorSessionRepository
                .findAdvisorSessionByStartupIdAndStartDateAndNotes(
                        startupId, dto.getStartDate(), dto.getNotes());

        if (dup != null
                && dup.getId() != null
                && !dup.getId().equals(session.getId())
                && dup.getStartup() != null
                && dup.getStartup().getId().equals(startup.getId())) {
            throw new ApiException("another session with same startDate and notes already exists");
        }

        // 7- apply updates
        session.setStartDate(dto.getStartDate());
        session.setDuration_minutes(dto.getDuration_minutes());
        session.setNotes(dto.getNotes());

        // 8- save
        advisorSessionRepository.save(session);
    }


    ///  8- delete advisor session by startup:
    public void deleteAdvisorSession(Integer sessionId, Integer startupId) {

        // 1- check session exists
        AdvisorSession session = advisorSessionRepository.findAdvisorSessionById(sessionId);
        if (session == null) {
            throw new ApiException("session not found");
        }

        // 2- check startup exists
        Startup startup = startupRepository.findStartupById(startupId);
        if (startup == null) {
            throw new ApiException("startup not found");
        }

        // 3- check ownership
        if (session.getStartup() == null || !session.getStartup().getId().equals(startup.getId())) {
            throw new ApiException("session and startup not belong to each other");
        }

        // 4- break relations
        session.setAdvisor(null);
        session.setStartup(null);


         if (!"pending".equals(session.getStatus())) {
             throw new ApiException("only pending sessions can be deleted");
         }

        // 5- delete session
        advisorSessionRepository.delete(session);
    }

}
