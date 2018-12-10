package com.chriniko.job.assignment.connector;

import com.chriniko.job.assignment.dto.RandomTextResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class RandomTextConnector {

    @Value("${random-text.connector.url}")
    private String randomTextUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public RandomTextConnector(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RandomTextResult produce(int numberOfParagraphs,
                                    int minNumberOfWordsPerSentence,
                                    int maxNumberOfWordsPerSentence) {

        String url = randomTextUrl
                + String.format("p-%d/%d-%d", numberOfParagraphs, minNumberOfWordsPerSentence, maxNumberOfWordsPerSentence);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "curl/7.52.1");
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<RandomTextResult> responseEntity
                = restTemplate.exchange(url, HttpMethod.GET, httpEntity, RandomTextResult.class);

        return responseEntity.getBody();
    }
}
