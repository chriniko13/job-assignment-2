package com.chriniko.job.assignment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TextProcessingResult {

    @JsonProperty("freq_word")
    private String mostFrequentWord;

    @JsonProperty("avg_paragraph_size")
    private long averageParagraphSize;

    @JsonProperty("avg_paragraph_processing_time_ns")
    private long averageParagraphProcessingTimeNs;

    @JsonProperty("avg_paragraph_processing_time_ms")
    private long averageParagraphProcessingTimeMs;

    @JsonProperty("total_processing_time_ns")
    private long totalProcessingTimeNs;

    @JsonProperty("total_processing_time_ms")
    private long totalProcessingTimeMs;

}
