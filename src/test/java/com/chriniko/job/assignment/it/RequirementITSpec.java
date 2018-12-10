package com.chriniko.job.assignment.it;

import com.chriniko.job.assignment.AssignmentApplication;
import com.chriniko.job.assignment.repository.TextProcessingEntryRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.jayway.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AssignmentApplication.class
)

public class RequirementITSpec {

    @Autowired
    public WireMockServer wireMockServer;

    @LocalServerPort
    protected Integer apiPort;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    private TextProcessingEntryRepository processingEntryRepository;

    @Before
    public void setup() {
        configureWireMocks();
    }

    private void configureWireMocks() {
        wireMockServer.resetAll();
    }

    private void registerRandomTextResultResponse(int paragraphStart, int paragraphEnd, final int minNumberOfWordsPerSentence, final int maxNumberOfWordsPerSentence) {

        IntStream.rangeClosed(paragraphStart, paragraphEnd).boxed().forEach(p -> {

            String fileName = "mock/randomResults_" + p + "_" + minNumberOfWordsPerSentence + "_" + maxNumberOfWordsPerSentence + ".json";
            try {
                String body = Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
                wireMockServer
                        .stubFor(
                                get(urlPathMatching("/api/giberish/p-" + p + "/" + minNumberOfWordsPerSentence + "-" + maxNumberOfWordsPerSentence))
                                        .willReturn(
                                                aResponse()
                                                        .withStatus(HttpURLConnection.HTTP_OK)
                                                        .withHeader("Content-Type", "application/json")
                                                        .withBody(body)
                                        )
                        );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    @Test
    public void requirement_works_as_expected() throws Exception {

        // given
        processingEntryRepository.deleteAll();

        int paragraphStart = 1;
        int paragraphEnd = 10;
        int minNumberOfWordsPerSentence = 25;
        int maxNumberOfWordsPerSentence = 50;

        String expectedHistoryResult
                = Resources.toString(Resources.getResource("mock/expectedHistoryResult1.json"), Charsets.UTF_8);

        URL resource = Resources.getResource("mock/expectedResult1.json");
        String expected = Resources.toString(resource, Charsets.UTF_8);

        String url = "http://localhost:" + apiPort + "/job-assignment/text";

        registerRandomTextResultResponse(paragraphStart,
                paragraphEnd,
                minNumberOfWordsPerSentence,
                maxNumberOfWordsPerSentence);

        String completeUrl = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("p_start", paragraphStart)
                .queryParam("p_end", paragraphEnd)
                .queryParam("w_count_min", minNumberOfWordsPerSentence)
                .queryParam("w_count_max", maxNumberOfWordsPerSentence)
                .toUriString();


        // when
        String results = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                null,
                String.class).getBody();

        // then
        assertNotNull(results);

        JSONAssert.assertEquals(expected, results, new CustomComparator(
                JSONCompareMode.STRICT,
                new Customization("total_processing_time_ms", (t1, t2) -> true),
                new Customization("total_processing_time_ns", (t1, t2) -> true),
                new Customization("avg_paragraph_processing_time_ms", (t1, t2) -> true),
                new Customization("avg_paragraph_processing_time_ns", (t1, t2) -> true)
        ));

        // and
        Awaitility
                .await()
                .timeout(10, TimeUnit.SECONDS)
                .until(() -> processingEntryRepository.findAll().size() == 1);

        // and
        String historyUrl = "http://localhost:" + apiPort + "/job-assignment/history";

        String historyResults = restTemplate.exchange(
                historyUrl,
                HttpMethod.GET,
                null,
                String.class).getBody();

        assertNotNull(historyResults);

        JSONAssert.assertEquals(expectedHistoryResult, historyResults, new CustomComparator(
                JSONCompareMode.STRICT,
                new Customization("content[0].id", (t1, t2) -> true),
                new Customization("content[0].timeOfRequest", (t1, t2) -> true),
                new Customization("content[0].averageParagraphProcessingTimeNs", (t1, t2) -> true),
                new Customization("content[0].averageParagraphProcessingTimeMs", (t1, t2) -> true),
                new Customization("content[0].totalProcessingTimeMs", (t1, t2) -> true),
                new Customization("content[0].totalProcessingTimeNs", (t1, t2) -> true)
        ));

    }
}
