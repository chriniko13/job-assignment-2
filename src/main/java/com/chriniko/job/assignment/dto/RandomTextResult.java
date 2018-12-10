package com.chriniko.job.assignment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString

@Builder
@AllArgsConstructor
public class RandomTextResult {

    private String type;

    @JsonProperty("amount")
    private String numberOfParagraphs;

    @JsonProperty("number")
    private String minNumberOfWordsPerSentence;

    @JsonProperty("number_max")
    private String maxNumberOfWordsPerSentence;

    @JsonProperty("format")
    private String textFormat;

    // Note: HH:MM:SS (UTC)
    @JsonProperty("time")
    private String timeOfTheRequest;

    @JsonProperty("text_out")
    private String text;

}
