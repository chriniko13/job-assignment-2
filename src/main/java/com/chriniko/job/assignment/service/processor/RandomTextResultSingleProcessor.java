package com.chriniko.job.assignment.service.processor;

import com.chriniko.job.assignment.dto.RandomTextResult;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RandomTextResultSingleProcessor implements RandomTextResultProcessor {

    private final Map<String, Integer> wordsFrequency = new HashMap<>();
    private long paragraphsSizeAccumulator = 0L;
    private long paragraphsProcessingTimeAccumulatorInNS = 0L;
    private long totalParagraphs = 0L;

    @Override
    public void process(List<RandomTextResult> randomTextResults) {
        randomTextResults.forEach(this::process);
    }

    @Override
    public long getParagraphsSizeAccumulator() {
        return paragraphsSizeAccumulator;
    }

    @Override
    public long getParagraphsProcessingTimeAccumulatorInNS() {
        return paragraphsProcessingTimeAccumulatorInNS;
    }

    @Override
    public long getTotalParagraphs() {
        return totalParagraphs;
    }

    @Override
    public Map<String, Integer> wordsFrequency() {
        return wordsFrequency;
    }

    private void process(RandomTextResult randomTextResult) {

        String text = randomTextResult.getText();

        String[] paragraphs = text.split("</p>\r");

        totalParagraphs += paragraphs.length;

        for (String paragraph : paragraphs) {

            long paragraphProcessingStartTime = System.nanoTime();

            paragraph = paragraph.replace(".", " ");
            paragraph = paragraph.replace("<p>", "");

            List<String> paragraphWords = Arrays.asList(paragraph.split(" "));

            paragraphsSizeAccumulator += paragraphWords.size();

            paragraphWords.forEach(paragraphWord -> {
                wordsFrequency.putIfAbsent(paragraphWord, 0);
                wordsFrequency.computeIfPresent(paragraphWord, (tW, count) -> count + 1);
            });

            long paragraphProcessingTotalTimeInNS = System.nanoTime() - paragraphProcessingStartTime;
            paragraphsProcessingTimeAccumulatorInNS += paragraphProcessingTotalTimeInNS;
        }

    }


}
