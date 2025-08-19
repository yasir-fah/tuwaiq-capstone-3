package com.fkhrayef.capstone3.Service;

import com.fkhrayef.capstone3.Api.ApiException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
// todo: you need to define the API KEY in the environment variables first!!
public class AiService {

    private final HashMap<String, String> promptTemplates = new HashMap<>();
    private String currentTemplate = "";
    private final ChatClient chatClient;

    public AiService(ChatClient.Builder chatClientBuilder) {


        //todo: write your prompt like the following:

        // when you want to send a new request:
        // 1: after setting the service instance
        // 2: call setConversation() with the prompt you want
        // 3: call chat() with your message, and the message will be added after the prompt

        // Prompt templates ex:

        /*promptTemplates.put("type_of_service", """
                You are a service classifier. Respond ONLY with one of these types:
                electrical, plumbing, cleaning, hvac, sound_system, lighting, carpentry, painting, security, supplies.
                
                Format your response using this exact template:
                [service_type]
                
                The Problem: 
                """);
        promptTemplates.put("urgency_level", """
                You are a problem urgency evaluator. Rate the urgency of the problem that the mosque is facing.
                Respond ONLY with one of these numbers:
                1, 2, 3, 4, 5
                
                1 = Not urgent at all
                5 = Extremely urgent
                
                Format your response using this exact template:
                [urgency_level]
                
                The Problem: 
                """);
        promptTemplates.put("estimate_time", """
                You are a problem time estimator. Decide the number of days that task need to fix by the maintenance team.
                Respond ONLY with the number of days in integer format.
                
                Format your response using this exact template:
                [number_of_days]
                
                The Problem: 
                """);
        promptTemplates.put("advice", """
                You are an expert maintenance technician. give a compact and simple step by step advice to your teammate to check and fix the problem.
                Respond with the steps ONLY, Line by line and number the steps.
                
                Format your response using this exact template:
                [1: do this step
                2: then do that step]
                
                The Problem: 
                """);*/
        chatClient = chatClientBuilder.build();
    }

    public void setConversation(String template) {
        if (!promptTemplates.containsKey(template)) {
            throw new ApiException("Template not found");
        }

        currentTemplate = promptTemplates.get(template);
    }


    public String chat(String message) {
        return chatClient
                .prompt()
                .system(currentTemplate)
                .user(message)
                .call()
                .content();
    }
}
