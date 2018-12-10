package com.chriniko.job.assignment.service.fetch;

import com.chriniko.job.assignment.connector.RandomTextConnector;
import com.chriniko.job.assignment.dto.RandomTextResult;
import com.chriniko.job.assignment.error.ProcessingException;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FetchRandomResults {

    @Value("${fetch-random-results.multithreaded-approach.enabled}")
    private boolean multiThreadedApproach;

    private final RandomTextConnector randomTextConnector;
    private final int availableProcessors;
    private final ThreadPoolExecutor workers;
    private final ObjectFactory<FetchRandomResultWorker> randomResultWorkerObjectFactory;

    @Autowired
    public FetchRandomResults(RandomTextConnector randomTextConnector,
                              @Qualifier("io") ThreadPoolExecutor workers,
                              ObjectFactory<FetchRandomResultWorker> randomResultWorkerObjectFactory) {
        this.randomTextConnector = randomTextConnector;
        this.availableProcessors = Runtime.getRuntime().availableProcessors();
        this.workers = workers;
        this.randomResultWorkerObjectFactory = randomResultWorkerObjectFactory;
    }

    public List<RandomTextResult> getRandomTextResults(int paragraphStart,
                                                       int paragraphEnd,
                                                       int minNumberOfWordsPerParagraph,
                                                       int maxNumberOfWordsPerParagraph) {

        List<Integer> allParagraphsIds = IntStream.rangeClosed(paragraphStart, paragraphEnd)
                .boxed()
                .collect(Collectors.toList());

        return multiThreadedApproach
                ? _getRandomTextResultsMultithreaded(allParagraphsIds,
                minNumberOfWordsPerParagraph,
                maxNumberOfWordsPerParagraph)
                : _getRandomTextResultsSingle(allParagraphsIds,
                minNumberOfWordsPerParagraph,
                maxNumberOfWordsPerParagraph);

    }

    private List<RandomTextResult> _getRandomTextResultsMultithreaded(List<Integer> allParagraphsIds,
                                                                      int minNumberOfWordsPerParagraph,
                                                                      int maxNumberOfWordsPerParagraph) {

        List<List<Integer>> partitionedParagraphsIds = Lists.partition(allParagraphsIds, availableProcessors);

        List<Future<List<RandomTextResult>>> futures = partitionedParagraphsIds
                .stream()
                .map(paragraphIdsToFetch -> {
                    FetchRandomResultWorker fetchRandomResultWorker = randomResultWorkerObjectFactory.getObject();
                    fetchRandomResultWorker.init(paragraphIdsToFetch, minNumberOfWordsPerParagraph, maxNumberOfWordsPerParagraph);
                    return fetchRandomResultWorker;
                })
                .collect(Collectors.toList())
                .stream()
                .map(workers::submit)
                .collect(Collectors.toList());

        return futures
                .stream()
                .map(f -> {
                    try {
                        return f.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new ProcessingException("Worker could not fetch random results.", e);
                    }
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<RandomTextResult> _getRandomTextResultsSingle(List<Integer> allParagraphsIds, int minNumberOfWordsPerParagraph, int maxNumberOfWordsPerParagraph) {
        return allParagraphsIds
                .stream()
                .map(paragraphIdx -> randomTextConnector.produce(paragraphIdx, minNumberOfWordsPerParagraph, maxNumberOfWordsPerParagraph))
                .collect(Collectors.toList());
    }


}
