package com.fkhrayef.capstone3.Service;

import com.fkhrayef.capstone3.Api.ApiException;
import com.fkhrayef.capstone3.DTOin.PaymentRequest;
import com.fkhrayef.capstone3.DTOout.MoyasarPaymentResponseDTO;
import com.fkhrayef.capstone3.DTOout.PaymentCreationResponseDTO;
import com.fkhrayef.capstone3.Model.*;
import com.fkhrayef.capstone3.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    // Only the repositories we actually need
    private final PaymentRepository paymentRepository;
    private final StartupRepository startupRepository;
    private final FreelancerRepository freelancerRepository;
    private final AdvisorRepository advisorRepository;
    private final FreelancerProjectRepository freelancerProjectRepository;
    private final AdvisorSessionRepository advisorSessionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final WhatsappService whatsappService;

    @Value("${moyasar.api.key}")
    private String apiKey;

    // Simple hardcoded values
    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1";
    private static final String CALLBACK_URL = "http://localhost:8080/api/v1/payments/callback";
    private static final String CURRENCY = "SAR";
    
    // Helper method to resolve transaction URL from Moyasar response
    private String resolveTransactionUrl(MoyasarPaymentResponseDTO moyasarResponse) {
        String transactionUrl = moyasarResponse.getTransaction_url();
        if (transactionUrl == null && moyasarResponse.getSource() != null) {
            transactionUrl = moyasarResponse.getSource().getTransaction_url();
        }
        return transactionUrl;
    }
    
    // Subscription pricing
    private static final Double PRO_MONTHLY_PRICE = 99.0;
    private static final Double PRO_YEARLY_PRICE = 990.0; // 10 months price
    private static final Double ENTERPRISE_MONTHLY_PRICE = 299.0;
    private static final Double ENTERPRISE_YEARLY_PRICE = 2990.0; // 10 months price
    
    // AI limits per plan
    private static final Integer PRO_AI_LIMIT = 1000; // 1000 AI requests per month
    private static final Integer ENTERPRISE_AI_LIMIT = 10000; // 10000 AI requests per month
    
    
    public MoyasarPaymentResponseDTO processPayment(PaymentRequest paymentRequest) {

        String url = MOYASAR_API_URL + "/payments";

        // create the request body
        String requestBody = String.format(
                "source[type]=card&source[name]=%s&source[number]=%s&source[cvc]=%s&" +
                        "source[month]=%s&source[year]=%s&amount=%d&currency=%s" +
                        "&callback_url=%s",
                paymentRequest.getName(),
                paymentRequest.getNumber(),
                paymentRequest.getCvc(),
                paymentRequest.getMonth(),
                paymentRequest.getYear(),
                (int) (paymentRequest.getAmount() * 100), // convert to the smallest currency unit
                paymentRequest.getCurrency(),
                CALLBACK_URL
        );

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // send the request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<MoyasarPaymentResponseDTO> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, MoyasarPaymentResponseDTO.class);

        // return the parsed DTO response
        return response.getBody();
    }

    public String getPaymentStatus(String paymentId) {
        // prepare headers with auth
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // create HTTP request entity
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // call Moyasar API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                MOYASAR_API_URL + "/payments/" + paymentId, HttpMethod.GET, entity, String.class
        );

        // return the response
        return response.getBody();
    }

    // Simple payment creation methods
    public PaymentCreationResponseDTO createFreelancerProjectPayment(Integer startupId, Integer projectId, PaymentRequest paymentRequest) {
        // Check if startup exists
        Startup startup = startupRepository.findStartupById(startupId);
        if (startup == null) {
            throw new ApiException("Startup not found");
        }
        
        // Check if project exists
        FreelancerProject project = freelancerProjectRepository.findFreelancerProjectById(projectId);
        if (project == null) {
            throw new ApiException("Project not found");
        }
        
        // Check if project belongs to this startup
        if (project.getStartup() == null || !project.getStartup().getId().equals(startupId)) {
            throw new ApiException("Project does not belong to this startup");
        }
        
        // Check if freelancer exists
        if (project.getFreelancer() == null) {
            throw new ApiException("No freelancer assigned to this project");
        }
        
        // Calculate hourly amount
        Double amount = project.getFreelancer().getHourlyRate() * project.getEstimatedHours();
        
        // Set payment details for Moyasar
        paymentRequest.setAmount(amount);
        paymentRequest.setDescription("Project payment for " + project.getProjectName());
        
        // Process payment through Moyasar
        MoyasarPaymentResponseDTO moyasarResponse = processPayment(paymentRequest);
        
        // Create payment record in database
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPaymentType("freelancer_project");
        payment.setStatus(moyasarResponse.getStatus() == null ? "pending" : moyasarResponse.getStatus().toLowerCase());
        payment.setCurrency(CURRENCY);
        payment.setDescription("Project payment for " + project.getProjectName());
        payment.setFreelancerProjectId(projectId);
        payment.setFreelancerId(project.getFreelancer().getId());
        payment.setStartup(startup);
        payment.setMoyasarPaymentId(moyasarResponse.getId()); // Clean access to ID
        
        Payment saved = paymentRepository.save(payment);
        
        // Balance will be updated when webhook confirms payment
        
        String transactionUrl = resolveTransactionUrl(moyasarResponse);
        String message = "Payment initiated. Please complete payment using the provided link.";
        return new PaymentCreationResponseDTO(saved, transactionUrl, moyasarResponse.getId(), saved.getStatus(), message);
    }

    public PaymentCreationResponseDTO createAdvisorSessionPayment(Integer startupId, Integer sessionId, PaymentRequest paymentRequest) {
        // Check if startup exists
        Startup startup = startupRepository.findStartupById(startupId);
        if (startup == null) {
            throw new ApiException("Startup not found");
        }
        
        // Check if advisor session exists
        AdvisorSession session = advisorSessionRepository.findAdvisorSessionById(sessionId);
        if (session == null) {
            throw new ApiException("Advisor session not found");
        }
        
        // Check if session belongs to this startup
        if (session.getStartup() == null || !session.getStartup().getId().equals(startupId)) {
            throw new ApiException("Advisor session does not belong to this startup");
        }
        
        // Check if advisor is assigned to session
        if (session.getAdvisor() == null) {
            throw new ApiException("No advisor assigned to this session");
        }
        
        // Use real session cost (or calculate from advisor hourly rate × duration if sessionCost is null)
        Double amount = session.getSessionCost();
        if (amount == null) {
            // Calculate from advisor hourly rate × duration if sessionCost not set
            if (session.getDuration_minutes() != null) {
                Double hourlyRate = session.getAdvisor().getHourlyRate();
                Double hours = session.getDuration_minutes() / 60.0;
                amount = hourlyRate * hours;
                
                // Update the session cost in database for future reference
                session.setSessionCost(amount);
                advisorSessionRepository.save(session);
            } else {
                throw new ApiException("Cannot calculate session cost: missing duration");
            }
        }
        
        // Set payment details for Moyasar
        paymentRequest.setAmount(amount);
        paymentRequest.setDescription("Advisor session payment");
        
        // Process payment through Moyasar
        MoyasarPaymentResponseDTO moyasarResponse = processPayment(paymentRequest);
        
        // Create payment record
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPaymentType("advisor_session");
        payment.setStatus(moyasarResponse.getStatus() == null ? "pending" : moyasarResponse.getStatus().toLowerCase());
        payment.setCurrency(CURRENCY);
        payment.setDescription("Advisor session with " + (session.getAdvisor() != null ? session.getAdvisor().getName() : "advisor"));
        payment.setAdvisorSessionId(sessionId);
        payment.setAdvisorId(session.getAdvisor() != null ? session.getAdvisor().getId() : null);
        payment.setStartup(startup);
        payment.setMoyasarPaymentId(moyasarResponse.getId()); // Clean access to ID
        
        Payment saved = paymentRepository.save(payment);
        
        // Balance will be updated when webhook confirms payment
        
        String transactionUrl = resolveTransactionUrl(moyasarResponse);
        String message = "Payment initiated. Please complete payment using the provided link.";
        return new PaymentCreationResponseDTO(saved, transactionUrl, moyasarResponse.getId(), saved.getStatus(), message);
    }

    public PaymentCreationResponseDTO createSubscriptionPayment(Integer startupId, String planType, String billingCycle, PaymentRequest paymentRequest) {
        // Check if startup exists
        Startup startup = startupRepository.findStartupById(startupId);
        if (startup == null) {
            throw new ApiException("Startup not found");
        }
        
        // Check if startup already has an active subscription
        Subscription existingSubscription = subscriptionRepository.findSubscriptionById(startupId);
        if (existingSubscription != null) {
            throw new ApiException("Startup already has a subscription. Please cancel the current subscription before subscribing to a new plan.");
        }
        
        // Validate plan type and billing cycle
        if (!planType.equals("pro") && !planType.equals("enterprise")) {
            throw new ApiException("Invalid plan type. Must be 'pro' or 'enterprise'");
        }
        if (!billingCycle.equals("monthly") && !billingCycle.equals("yearly")) {
            throw new ApiException("Invalid billing cycle. Must be 'monthly' or 'yearly'");
        }
        
        // Calculate amount based on plan and billing cycle
        Double amount;
        if (planType.equals("pro")) {
            amount = billingCycle.equals("monthly") ? PRO_MONTHLY_PRICE : PRO_YEARLY_PRICE;
        } else {
            amount = billingCycle.equals("monthly") ? ENTERPRISE_MONTHLY_PRICE : ENTERPRISE_YEARLY_PRICE;
        }
        
        // Set payment details for Moyasar
        paymentRequest.setAmount(amount);
        paymentRequest.setDescription("Subscription: " + planType + " (" + billingCycle + ")");
        
        // Process payment through Moyasar
        MoyasarPaymentResponseDTO moyasarResponse = processPayment(paymentRequest);
        
        // Create payment record
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPaymentType("subscription");
        payment.setStatus(moyasarResponse.getStatus() == null ? "pending" : moyasarResponse.getStatus().toLowerCase());
        payment.setCurrency(CURRENCY);
        payment.setDescription("Subscription: " + planType + " (" + billingCycle + ") - " + amount + " " + CURRENCY);
        payment.setStartup(startup);
        payment.setMoyasarPaymentId(moyasarResponse.getId());
        
        // Set null for unused reference fields to avoid constraint issues
        payment.setFreelancerProjectId(null);
        payment.setAdvisorSessionId(null);
        payment.setSubscriptionId(null);
        payment.setFreelancerId(null);
        payment.setAdvisorId(null);
        
        Payment saved = paymentRepository.save(payment);
        
        // Don't create subscription yet - wait for payment completion via webhook
        // The subscription will be created when Moyasar sends "PAID" status
        
        String transactionUrl = resolveTransactionUrl(moyasarResponse);
        String message = "Payment initiated. Please complete payment using the provided link.";
        return new PaymentCreationResponseDTO(saved, transactionUrl, moyasarResponse.getId(), saved.getStatus(), message);
    }

    public Payment getPaymentById(Integer paymentId) {
        Payment payment = paymentRepository.findPaymentById(paymentId);
        if (payment == null) {
            throw new ApiException("Payment not found");
        }
        return payment;
    }
    
    /**
     * Handle payment completion - called when Moyasar webhook confirms payment
     * Creates subscription for subscription payments, updates balances for other payments
     */
    public void handlePaymentCompletion(String moyasarPaymentId, String moyasarStatus) {
        // Find payment by Moyasar ID
        Payment payment = paymentRepository.findByMoyasarPaymentId(moyasarPaymentId);
        if (payment == null) {
            throw new ApiException("Payment not found with Moyasar ID: " + moyasarPaymentId);
        }
        
        // Update payment status
        String newStatus = (moyasarStatus == null ? "pending" : moyasarStatus.toLowerCase());
        payment.setStatus(newStatus);
        paymentRepository.save(payment);
        
        // Handle different payment types
        if ("subscription".equals(payment.getPaymentType()) && ("paid".equals(newStatus) || "captured".equals(newStatus))) {
            createSubscriptionFromPayment(payment);
        } else if ("freelancer_project".equals(payment.getPaymentType()) && ("paid".equals(newStatus) || "captured".equals(newStatus))) {
            updateFreelancerBalance(payment);
            activateFreelancerProject(payment);
        } else if ("advisor_session".equals(payment.getPaymentType()) && ("paid".equals(newStatus) || "captured".equals(newStatus))) {
            updateAdvisorBalance(payment);
            activateAdvisorSession(payment);
        }
    }
    
    /**
     * Create subscription from completed payment
     */
    private void createSubscriptionFromPayment(Payment payment) {
        try {
            // Parse plan details from payment description
            String description = payment.getDescription();
            // Format: "Subscription: pro (monthly) - 99.0 SAR"
            String[] parts = description.split(" - ")[0].split(": ")[1].split(" \\(");
            String planType = parts[0];
            String billingCycle = parts[1].replace(")", "");
            
            Integer startupId = payment.getStartup().getId();
            
                    // No need to check for existing subscription since we delete on cancel
        // This prevents JPA session conflicts when creating new subscriptions
            
            // Create new subscription
            Subscription subscription = new Subscription();
            // DO NOT set ID manually - @MapsId will handle it from startup
            subscription.setPlanType(planType);
            subscription.setBillingCycle(billingCycle);
            subscription.setStatus("active");
            subscription.setStartDate(LocalDateTime.now());
            
            // Set end date based on billing cycle
            if (billingCycle.equals("monthly")) {
                subscription.setEndDate(LocalDateTime.now().plusMonths(1));
            } else {
                subscription.setEndDate(LocalDateTime.now().plusYears(1));
            }
            
            // Set AI limits based on plan
            if (planType.equals("pro")) {
                subscription.setAiLimit(PRO_AI_LIMIT);
            } else {
                subscription.setAiLimit(ENTERPRISE_AI_LIMIT);
            }
            
            subscription.setPrice(payment.getAmount());
            subscription.setStartup(payment.getStartup()); // This will set the ID via @MapsId
            
            subscriptionRepository.save(subscription);
            
            // Link payment to subscription
            payment.setSubscriptionId(startupId);
            paymentRepository.save(payment);
            
        } catch (Exception e) {
            throw new ApiException("Failed to create subscription from payment: " + e.getMessage());
        }
    }
    
    /**
     * Update freelancer earnings when project payment is completed
     */
    private void updateFreelancerBalance(Payment payment) {
        if (payment.getFreelancerId() != null) {
            Freelancer freelancer = freelancerRepository.findFreelancerById(payment.getFreelancerId());
            if (freelancer != null) {
                freelancer.setTotalEarnings(freelancer.getTotalEarnings() + payment.getAmount());
                freelancerRepository.save(freelancer);
            }
        }
    }
    
    /**
     * Update advisor earnings when session payment is completed
     */
    private void updateAdvisorBalance(Payment payment) {
        if (payment.getAdvisorId() != null) {
            Advisor advisor = advisorRepository.findAdvisorById(payment.getAdvisorId());
            if (advisor != null) {
                advisor.setTotalEarnings(advisor.getTotalEarnings() + payment.getAmount());
                advisorRepository.save(advisor);
            }
        }
    }
    
    /**
     * Activate advisor session after successful payment
     */
    private void activateAdvisorSession(Payment payment) {
        if (payment.getAdvisorSessionId() != null) {
            AdvisorSession session = advisorSessionRepository.findAdvisorSessionById(payment.getAdvisorSessionId());
            if (session != null) {
                session.setStatus("confirmed");
                advisorSessionRepository.save(session);
            }
        }
    }
    
    /**
     * Activate freelancer project after successful payment
     */
    private void activateFreelancerProject(Payment payment) {
        if (payment.getFreelancerProjectId() != null) {
            FreelancerProject project = freelancerProjectRepository.findFreelancerProjectById(payment.getFreelancerProjectId());
            if (project != null) {
                // Move from 'accepted' to 'active' after payment
                project.setStatus("active");
                freelancerProjectRepository.save(project);
            }
        }
    }
    
    /**
     * Cancel a startup's active subscription
     */
    public void cancelSubscription(Integer startupId) {
        // Check if startup exists
        Startup startup = startupRepository.findStartupById(startupId);
        if (startup == null) {
            throw new ApiException("Startup not found");
        }
        
        // Find and delete subscription completely
        Subscription subscription = subscriptionRepository.findSubscriptionById(startupId);
        if (subscription == null) {
            throw new ApiException("No subscription found for this startup");
        }
        
        if (!"active".equals(subscription.getStatus())) {
            throw new ApiException("Subscription is not active. Current status: " + subscription.getStatus());
        }
        
        // Properly handle the bidirectional relationship
        // Since @OneToOne with @PrimaryKeyJoinColumn, we need to clear the reference
        // This prevents JPA from trying to maintain the relationship
        startup.setSubscription(null);
        startupRepository.save(startup);
        
        // Now we can safely delete the subscription
        subscriptionRepository.delete(subscription);
        
        // Verify the subscription was deleted
        if (subscriptionRepository.findSubscriptionById(startupId) != null) {
            throw new ApiException("Failed to delete subscription");
        }
    }
    
    /**
     * Get subscription status for a startup
     */
    public Subscription getSubscriptionStatus(Integer startupId) {
        // Check if startup exists
        Startup startup = startupRepository.findStartupById(startupId);
        if (startup == null) {
            throw new ApiException("Startup not found");
        }
        
        // Find subscription
        Subscription subscription = subscriptionRepository.findSubscriptionById(startupId);
        if (subscription == null) {
            throw new ApiException("No subscription found for this startup");
        }
        
        return subscription;
    }
    
    /**
     * Scheduled task to handle subscription renewals
     * Runs daily at midnight to check for expired subscriptions
     */
    @Scheduled(cron = "0 0 0 * * *") // Daily at midnight
    public void handleSubscriptionRenewals() {
        try {
            // Find all active subscriptions that are expiring today or have expired
            List<Subscription> expiringSubscriptions = subscriptionRepository.findActiveSubscriptionsExpiringSoon(LocalDateTime.now().plusDays(1));
            
            for (Subscription subscription : expiringSubscriptions) {
                try {
                    processSubscriptionRenewal(subscription);
                } catch (Exception e) {
                    // Continue with other subscriptions even if one fails
                }
            }
        } catch (Exception e) {
            // Renewal job failed, will retry tomorrow
        }
    }
    
    /**
     * Process renewal for a single subscription
     */
    private void processSubscriptionRenewal(Subscription subscription) {
        Startup startup = subscription.getStartup();
        
        // Mark current subscription as expired
        subscription.setStatus("expired");
        subscriptionRepository.save(subscription);
        
        // Create automatic renewal payment through Moyasar
        try {
            // Create payment request with calculated amount
            PaymentRequest renewalRequest = new PaymentRequest();
            renewalRequest.setAmount(subscription.getPrice());
            renewalRequest.setDescription("Auto-renewal: " + subscription.getPlanType() + " (" + subscription.getBillingCycle() + ")");
            renewalRequest.setCurrency("SAR");
            
            // Process payment through Moyasar to get payment link
            MoyasarPaymentResponseDTO moyasarResponse = processPayment(renewalRequest);
            
            // Create renewal payment record
            Payment renewalPayment = new Payment();
            renewalPayment.setAmount(subscription.getPrice());
            renewalPayment.setPaymentType("subscription");
            renewalPayment.setStatus(moyasarResponse.getStatus() == null ? "pending" : moyasarResponse.getStatus().toLowerCase());
            renewalPayment.setCurrency("SAR");
            renewalPayment.setDescription("Auto-renewal: " + subscription.getPlanType() + " (" + subscription.getBillingCycle() + ")");
            renewalPayment.setStartup(startup);
            renewalPayment.setMoyasarPaymentId(moyasarResponse.getId());
            
            // Set null for unused reference fields
            renewalPayment.setFreelancerProjectId(null);
            renewalPayment.setAdvisorSessionId(null);
            renewalPayment.setSubscriptionId(null);
            renewalPayment.setFreelancerId(null);
            renewalPayment.setAdvisorId(null);
            
            paymentRepository.save(renewalPayment);
            
            // Send payment link to user via WhatsApp
            String paymentLink = resolveTransactionUrl(moyasarResponse);
            String message = "Your subscription has expired. Click here to renew: " + paymentLink;
            
            // Get phone from founder (assuming startup has at least one founder)
            if (startup.getFounders() != null && !startup.getFounders().isEmpty()) {
                String founderPhone = startup.getFounders().iterator().next().getPhone();
                whatsappService.sendTextMessage(message, founderPhone);
            }
            
        } catch (Exception e) {
            // Subscription is still marked as expired even if payment creation fails
        }
    }
    
    /**
     * Get all active subscriptions that are expiring soon (within 1 day)
     */
    public List<Subscription> getExpiringSubscriptions() {
        return subscriptionRepository.findActiveSubscriptionsExpiringSoon(LocalDateTime.now().plusDays(1));
    }
}
