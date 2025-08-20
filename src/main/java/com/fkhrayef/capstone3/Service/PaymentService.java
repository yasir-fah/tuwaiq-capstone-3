package com.fkhrayef.capstone3.Service;

import com.fkhrayef.capstone3.DTOin.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${moyasar.api.key}")
    private String apiKey;

    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1/payments/";

    public ResponseEntity<String> processPayment(PaymentRequest paymentRequest) {

        String url = "https://api.moyasar.com/v1/payments/"; // TODO remove and use the one above?

        // TODO Change the callback url to whatever we want
        String callbackUrl = "http://localhost:8080/api/v1/payments/callback";

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
                callbackUrl
        );

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // send the request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, String.class);

        // return the API response

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
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
                MOYASAR_API_URL + paymentId, HttpMethod.GET, entity, String.class
        );

        // return the response
        return response.getBody();
    }
}
