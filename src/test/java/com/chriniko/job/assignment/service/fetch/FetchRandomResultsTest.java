package com.chriniko.job.assignment.service.fetch;

import com.chriniko.job.assignment.connector.RandomTextConnector;
import com.chriniko.job.assignment.dto.RandomTextResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectFactory;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class FetchRandomResultsTest {

    private FetchRandomResults fetchRandomResults;

    private RandomTextConnector mockedRandomTextConnector;

    private ThreadPoolExecutor mockedWorkers;
    private ObjectFactory<FetchRandomResultWorker> randomResultWorkerObjectFactory;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        mockedRandomTextConnector = Mockito.mock(RandomTextConnector.class);
        mockedWorkers = Mockito.mock(ThreadPoolExecutor.class);
        randomResultWorkerObjectFactory = Mockito.mock(ObjectFactory.class);

        fetchRandomResults = new FetchRandomResults(
                mockedRandomTextConnector,
                mockedWorkers,
                randomResultWorkerObjectFactory
        );
    }

    @Test
    public void getRandomTextResults() {

        // given
        RandomTextResult randomTextResult = RandomTextResult.builder()
                .type("some-type")
                .text("text")
                .textFormat("tF")
                .build();

        Mockito.when(mockedRandomTextConnector.produce(
                Mockito.anyInt(),
                Mockito.anyInt(),
                Mockito.anyInt()))
                .thenReturn(randomTextResult);

        // when
        List<RandomTextResult> results = fetchRandomResults.getRandomTextResults(
                1,
                3,
                6,
                7);


        // then
        Assert.assertNotNull(results.get(0));

    }
}