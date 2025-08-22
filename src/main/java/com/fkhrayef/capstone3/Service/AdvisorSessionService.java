package com.fkhrayef.capstone3.Service;

import com.fkhrayef.capstone3.Api.ApiException;
import com.fkhrayef.capstone3.DTOin.AdvisorSessionDTO;
import com.fkhrayef.capstone3.Model.*;
import com.fkhrayef.capstone3.Repository.AdvisorRepository;
import com.fkhrayef.capstone3.Repository.AdvisorSessionRepository;
import com.fkhrayef.capstone3.Repository.PaymentRepository;
import com.fkhrayef.capstone3.Repository.StartupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvisorSessionService {

    private final AdvisorSessionRepository advisorSessionRepository;
    private final StartupRepository startupRepository;
    private final AdvisorRepository advisorRepository;
    private final WebexService webexService;
    private final FirefliesAiApiService firefliesAiApiService;
    private final PaymentRepository paymentRepository;

    ///  1- get sessions of one startup
    public List<AdvisorSession> getAllAdvisorSessionsFromStartup(Integer startupId){

       // 1- check if startup exist:
       Startup startup = startupRepository.findStartupById(startupId);
       if(startup == null) {
           throw new ApiException("startup not found");
       }
       return advisorSessionRepository.findAdvisorSessionByStartupId(startup.getId());
   }


   /// 2- startup create session targeting a specific advisor (single step)
   public void addAdvisorSessionByStartup(Integer startupId, Integer advisorId, AdvisorSessionDTO dto){

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

       // 3- check if advisor exist:
       Advisor advisor = advisorRepository.findAdvisorById(advisorId);
       if(advisor == null){
           throw new ApiException("advisor not found");
       }

       // 4- add the request values:
       AdvisorSession advisorSession = new AdvisorSession();
       advisorSession.setTitle(dto.getTitle());
       advisorSession.setStartDate(dto.getStartDate());
       advisorSession.setDuration_minutes(dto.getDuration_minutes());
       advisorSession.setNotes(dto.getNotes());
       advisorSession.setStatus("pending");

       // 5- link session with startup & advisor then save the session:
       advisorSession.setStartup(startup);
       advisorSession.setAdvisor(advisor);
       advisorSessionRepository.save(advisorSession);
   }

   /// 3- advisor accepts session:
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


    /// 4- advisor reject session:
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
        session.setStatus("rejected"); // Advisor rejected this session
        advisorSessionRepository.save(session);
    }


    /// 5- allow startup to cancel their request:
    public void startupCancelAdvisorRequest(Integer startupId, Integer sessionId){

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

        // 3- check session belongs to same startup
        if(session.getStartup().getId() == null
                || !session.getStartup().getId().equals(startup.getId())){
            throw new ApiException("session and startup not belong to each other");
        }

        // 4- check if status of session still pending:
        if(!session.getStatus().equals("pending")){
            throw new ApiException("status should be pending to cancel advising session");
        }

        session.setStatus("cancelled"); // Mark as cancelled, keep advisor for audit history
        advisorSessionRepository.save(session);
    }

    ///  6- update advisor session;
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


    ///  7- delete advisor session by startup:
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

        // 4- check status
        if (!"pending".equals(session.getStatus())) {
            throw new ApiException("only pending sessions can be deleted");
        }

        // 5- break relations
        session.setAdvisor(null);
        session.setStartup(null);

        // 6- delete session
        advisorSessionRepository.delete(session);
    }

    public void startMeeting(Integer sessionId){
        AdvisorSession session = advisorSessionRepository.findAdvisorSessionById(sessionId);
        if (session == null) {
            throw new ApiException("session not found");
        }

        // Search for payment with this session id:
        Payment payment = paymentRepository.findPaymentByAdvisorSessionId(session.getId());
        if(payment == null){
            throw new ApiException("this Session did not get paid");
        }

        // Check from payment's status:
        if(!payment.getStatus().equals("paid")){
            throw new ApiException("Payment status should be paid to start meeting");
        }

        // Start meeting
        webexService.startMeeting(session.getTitle(), session.getStartDate(), session.getDuration_minutes(),getAllEmails(sessionId));
    }

    public String getSummary(String meetingLink){
        return firefliesAiApiService.getMeetingSummary(meetingLink);
    }

    public List<String> getAllEmails(Integer sessionId){
        AdvisorSession advisorSession = advisorSessionRepository.findAdvisorSessionById(sessionId);
        if (advisorSession == null) {
            throw new ApiException("session not found");
        }
        List<String> emails = new ArrayList<>();
        if (advisorSession.getAdvisor().getEmail() == null){
            throw new ApiException("advisor email was not found");
        }
        emails.add(advisorSession.getAdvisor().getEmail());
        for (Founder founder : advisorSession.getStartup().getFounders()) {
            if (founder.getEmail() != null){
                emails.add(founder.getEmail());
            }
        }
        return emails;
    }



}
