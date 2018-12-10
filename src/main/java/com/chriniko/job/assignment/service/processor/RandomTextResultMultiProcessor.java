package com.chriniko.job.assignment.service.processor;

import com.chriniko.job.assignment.dto.RandomTextResult;
import com.chriniko.job.assignment.error.ProcessingException;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.LongAdder;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RandomTextResultMultiProcessor implements RandomTextResultProcessor {

    @Value("${multi-text-processing.partition-size}")
    private int partitionSize;

    private final ThreadPoolExecutor workers;

    private final Map<String, Integer> wordsFrequency = new ConcurrentHashMap<>();
    private final LongAdder paragraphsSizeAccumulator = new LongAdder();
    private final LongAdder paragraphsProcessingTimeAccumulatorInNS = new LongAdder();
    private final LongAdder totalParagraphs = new LongAdder();

    @Autowired
    public RandomTextResultMultiProcessor(@Qualifier("computation") ThreadPoolExecutor workers) {
        this.workers = workers;
    }

    @Override
    public void process(List<RandomTextResult> randomTextResults) {

        final List<List<RandomTextResult>> partitionedResults
                = Lists.partition(randomTextResults, partitionSize);

        final CountDownLatch workFinished = new CountDownLatch(partitionedResults.size());

        partitionedResults.forEach(results -> {
            Runnable task = process(workFinished, results);
            workers.submit(task);
        });

        try {
            workFinished.await();
        } catch (InterruptedException e) {
            throw new ProcessingException("Worker could not process random results", e);
        }
    }

    private Runnable process(CountDownLatch workFinished, List<RandomTextResult> results) {
        return () -> {
            results.forEach(result -> {

                String text = result.getText();
                String[] paragraphs = text.split("</p>\r");

                totalParagraphs.add(paragraphs.length);

                for (String paragraph : paragraphs) {

                    long paragraphProcessingStartTime = System.nanoTime();

                    paragraph = paragraph.replace(".", " ");
                    paragraph = paragraph.replace("<p>", "");

                    List<String> paragraphWords = Arrays.asList(paragraph.split(" "));

                    paragraphsSizeAccumulator.add(paragraphWords.size());

                    paragraphWords.forEach(paragraphWord -> {
                        wordsFrequency.putIfAbsent(paragraphWord, 0);
                        wordsFrequency.computeIfPresent(paragraphWord, (tW, count) -> count + 1);
                    });

                    long paragraphProcessingTotalTimeInNS = System.nanoTime() - paragraphProcessingStartTime;
                    paragraphsProcessingTimeAccumulatorInNS.add(paragraphProcessingTotalTimeInNS);
                }

            });

            workFinished.countDown();
        };
    }

    @Override
    public long getParagraphsSizeAccumulator() {
        return paragraphsSizeAccumulator.sum();
    }

    @Override
    public long getParagraphsProcessingTimeAccumulatorInNS() {
        return paragraphsProcessingTimeAccumulatorInNS.sum();
    }

    @Override
    public long getTotalParagraphs() {
        return totalParagraphs.sum();
    }

    @Override
    public Map<String, Integer> wordsFrequency() {
        return wordsFrequency;
    }
}
