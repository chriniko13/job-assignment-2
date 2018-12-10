package com.chriniko.job.assignment.service;

import com.chriniko.job.assignment.domain.TextProcessingEntry;
import com.chriniko.job.assignment.dto.RandomTextResult;
import com.chriniko.job.assignment.dto.TextProcessingResult;
import com.chriniko.job.assignment.repository.TextProcessingEntryRepository;
import com.chriniko.job.assignment.service.fetch.FetchRandomResults;
import com.chriniko.job.assignment.service.processor.RandomTextResultMultiProcessor;
import com.chriniko.job.assignment.service.processor.RandomTextResultProcessor;
import com.chriniko.job.assignment.service.processor.RandomTextResultSingleProcessor;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Service
public class TextProcessingService {

    private final FetchRandomResults fetchRandomResults;

    @Value("${multi-text-processing.enabled}")
    private boolean multiThreadApproach;
    private final ObjectFactory<RandomTextResultSingleProcessor> randomTextResultSingleProcessorObjectFactory;
    private final ObjectFactory<RandomTextResultMultiProcessor> randomTextResultMultiProcessorObjectFactory;

    private final TextProcessingEntryRepository textProcessingEntryRepository;
    private final DozerBeanMapper dozerBeanMapper;
    private final ThreadPoolExecutor workers;

    @Autowired
    public TextProcessingService(FetchRandomResults fetchRandomResults,
                                 ObjectFactory<RandomTextResultSingleProcessor> randomTextResultSingleProcessorObjectFactory,
                                 ObjectFactory<RandomTextResultMultiProcessor> randomTextResultMultiProcessorObjectFactory,
                                 TextProcessingEntryRepository textProcessingEntryRepository,
                                 DozerBeanMapper dozerBeanMapper,
                                 @Qualifier("io") ThreadPoolExecutor workers) {
        this.fetchRandomResults = fetchRandomResults;
        this.randomTextResultSingleProcessorObjectFactory = randomTextResultSingleProcessorObjectFactory;
        this.randomTextResultMultiProcessorObjectFactory = randomTextResultMultiProcessorObjectFactory;
        this.textProcessingEntryRepository = textProcessingEntryRepository;
        this.dozerBeanMapper = dozerBeanMapper;
        this.workers = workers;
    }

    public TextProcessingResult process(int paragraphStart,
                                        int paragraphEnd,
                                        int minNumberOfWordsPerParagraph,
                                        int maxNumberOfWordsPerParagraph) {

        long startTime = System.nanoTime();

        List<RandomTextResult> randomTextResults
                = fetchRandomResults.getRandomTextResults(paragraphStart, paragraphEnd, minNumberOfWordsPerParagraph, maxNumberOfWordsPerParagraph);

        RandomTextResultProcessor randomTextResultProcessor = multiThreadApproach
                ? randomTextResultMultiProcessorObjectFactory.getObject()
                : randomTextResultSingleProcessorObjectFactory.getObject();
        randomTextResultProcessor.process(randomTextResults);

        Map.Entry<String, Integer> mostFrequentWordEntry = randomTextResultProcessor.findMostFrequentWord();

        long totalParagraphs = randomTextResultProcessor.getTotalParagraphs();

        long averageParagraphSize
                = randomTextResultProcessor.getParagraphsSizeAccumulator() / totalParagraphs;

        long averageParagraphProcessingTimeInNS
                = randomTextResultProcessor.getParagraphsProcessingTimeAccumulatorInNS() / totalParagraphs;
        long averageParagraphProcessingTimeInMS = TimeUnit.MILLISECONDS.convert(averageParagraphProcessingTimeInNS, TimeUnit.NANOSECONDS);

        long totalProcessingTimeInNS = System.nanoTime() - startTime;
        long totalProcessingTimeInMS = TimeUnit.MILLISECONDS.convert(totalProcessingTimeInNS, TimeUnit.NANOSECONDS);

        TextProcessingResult textProcessingResult = new TextProcessingResult(
                mostFrequentWordEntry.getKey(),
                averageParagraphSize,
                averageParagraphProcessingTimeInNS,
                averageParagraphProcessingTimeInMS,
                totalProcessingTimeInNS,
                totalProcessingTimeInMS);

        workers.submit(() -> persist(textProcessingResult));

        return textProcessingResult;
    }

    private void persist(TextProcessingResult textProcessingResult) {
        TextProcessingEntry textProcessingEntry = dozerBeanMapper.map(textProcessingResult, TextProcessingEntry.class);
        textProcessingEntry.setId(UUID.randomUUID().toString());
        textProcessingEntry.setTimeOfRequest(Instant.now().toString());

        textProcessingEntryRepository.save(textProcessingEntry);
    }
}
