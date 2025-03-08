package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
@Service
@Slf4j
@RequiredArgsConstructor
public class FlaskClientService {



    private final RestTemplate restTemplate = new RestTemplate();
    private final String flaskUrl = "http://localhost:5000/similarity";

    public double getSimilarityScore(String text1, String text2) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text1", text1);
        requestBody.put("text2", text2);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, request, Map.class);

        return response.getBody() != null ? (double) response.getBody().get("score") : 0.0;
    }
}
