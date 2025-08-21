package com.fkhrayef.capstone3.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fkhrayef.capstone3.Api.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.HashMap;

@Service
public class FirefliesAiApiService {

    private final WebClient webClient;

    @Value("${fireflies.api.key}")
    private String apiKey;

    public FirefliesAiApiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.fireflies.ai/graphql")
                .build();
    }

    public String getMeetingSummary(String meetingLink) {
        // First find the transcript ID, then get the summary - blocking approach
        String transcriptId = findTranscriptIdByMeetingLink(meetingLink);

        String query = """
        query Transcript($transcriptId: String!) {
          transcript(id: $transcriptId) {
            summary {
              action_items
              overview
              short_summary
              keywords
              topics_discussed
            }
          }
        }
        """;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        Map<String, Object> variables = new HashMap<>();
        variables.put("transcriptId", transcriptId);
        requestBody.put("variables", variables);

        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(jsonResponse -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(jsonResponse);


                        JsonNode summaryNode = root.path("data")
                                .path("transcript")
                                .path("summary")
                                .path("short_summary");



                        return summaryNode.asText();
                    } catch (Exception e) {

                        throw new ApiException("Error parsing summary response");
                    }
                })
                .block();
    }

    public String findTranscriptIdByMeetingLink(String targetMeetingLink) {
        String query = """
        query {
          transcripts {
            id
            meeting_link
            title
          }
        }
        """;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(jsonResponse -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(jsonResponse);

                        JsonNode transcripts = root.path("data").path("transcripts");

                        //  find matching meeting_link
                        for (JsonNode transcript : transcripts) {
                            String meetingLink = transcript.path("meeting_link").asText();
                            if (targetMeetingLink.equals(meetingLink)) {
                                String id = transcript.path("id").asText();
                                if (id.isEmpty()) {
                                    throw new ApiException("Transcript ID is empty");
                                }
                                return id;
                            }
                        }

                        throw new ApiException("Transcript not found for meeting link");

                    } catch (Exception e) {

                        throw new ApiException(e.getMessage());
                    }
                })
                .block();
    }
}