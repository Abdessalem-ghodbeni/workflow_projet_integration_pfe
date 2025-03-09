package com.abdessalem.finetudeingenieurworkflow.Services.ServiceImplementation;

import com.abdessalem.finetudeingenieurworkflow.Entites.SubjectCandidatureDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Slf4j
@RequiredArgsConstructor
public class FlaskClientService {
 private final RestTemplate restTemplate = new RestTemplate();
    private final String flaskUrl = "http://localhost:5000/similarity";

    private final String flaskApiUrl = "http://localhost:5000/similarity";



    public Map<String, Object> sendDataToFlask(List<SubjectCandidatureDTO> data) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<SubjectCandidatureDTO>> requestEntity = new HttpEntity<>(data, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    flaskApiUrl, HttpMethod.POST, requestEntity, Map.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l’appel à Flask : " + e.getMessage());
        }
    }
}
