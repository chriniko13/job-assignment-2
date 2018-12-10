package com.chriniko.job.assignment.service.fetch;

import com.chriniko.job.assignment.connector.RandomTextConnector;
import com.chriniko.job.assignment.dto.RandomTextResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FetchRandomResultWorker implements Callable<List<RandomTextResult>> {

    private final RandomTextConnector randomTextConnector;

    private List<Integer> paragraphsIdsToFetch;
    private int minNumberOfWordsPerParagraph;
    private int maxNumberOfWordsPerParagraph;

    @Autowired
    private FetchRandomResultWorker(RandomTextConnector randomTextConnector) {
        this.randomTextConnector = randomTextConnector;
    }

    void init(List<Integer> paragraphsIdsToFetch,
              int minNumberOfWordsPerParagraph,
              int maxNumberOfWordsPerParagraph) {
        this.paragraphsIdsToFetch = paragraphsIdsToFetch;
        this.minNumberOfWordsPerParagraph = minNumberOfWordsPerParagraph;
        this.maxNumberOfWordsPerParagraph = maxNumberOfWordsPerParagraph;
    }

    @Override
    public List<RandomTextResult> call() {
        return paragraphsIdsToFetch
                .stream()
                .map(paragraphIdx -> randomTextConnector.produce(paragraphIdx,
                        minNumberOfWordsPerParagraph,
                        maxNumberOfWordsPerParagraph))
                .collect(Collectors.toList());
    }
}