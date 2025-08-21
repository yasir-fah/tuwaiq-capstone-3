package com.fkhrayef.capstone3.Service;

import com.fkhrayef.capstone3.Api.ApiException;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
public class WebexService {
    @Value("${WEBEX_KEY}")
    private String webexKey;
    private final OkHttpClient client;

    public WebexService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void startMeeting(String title, LocalDateTime meetingDateTime, Integer duration_minutes) throws ApiException {

        MediaType mediaType = MediaType.parse("application/json");

        ZonedDateTime start = meetingDateTime.atZone(ZoneId.of("Asia/Riyadh"));
        ZonedDateTime end = start.plusMinutes(duration_minutes);

        String startDate = start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String endDate   = end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String json = "{"
                      + "\"title\": \"" + title + "\","
                      + "\"start\": \"" + startDate + "\","
                      + "\"end\": \"" + endDate + "\","
                      + "\"invitees\": ["
                      + "  {"
                      + "    \"email\": \"fred@fireflies.ai\","
                      + "    \"displayName\": \"AI\""
                      + "  }"
                      + "]"
                      + "}";


        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url("https://webexapis.com/v1/meetings")
                .addHeader("Authorization", "Bearer "+ webexKey )
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json;charset=UTF-8")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new ApiException(response.message());
            }

        } catch (Exception e) {
            throw new ApiException("Unexpected error while starting meeting: " + e.getMessage());
        }
    }
}
