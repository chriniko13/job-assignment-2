package com.chriniko.job.assignment.service.processor;

import com.chriniko.job.assignment.dto.RandomTextResult;
import com.chriniko.job.assignment.error.ProcessingException;

import java.util.List;
import java.util.Map;

public interface RandomTextResultProcessor {

    void process(List<RandomTextResult> randomTextResults);

    long getParagraphsSizeAccumulator();

    long getParagraphsProcessingTimeAccumulatorInNS();

    long getTotalParagraphs();

    Map<String, Integer> wordsFrequency();

    default Map.Entry<String, Integer> findMostFrequentWord() {
        return wordsFrequency()
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new ProcessingException("could not extract most frequent word"));
    }

}
