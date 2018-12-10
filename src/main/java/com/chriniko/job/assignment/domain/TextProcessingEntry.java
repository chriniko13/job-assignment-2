package com.chriniko.job.assignment.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = {"id"})

@Document(collection = "text-processing-entries")
public class TextProcessingEntry {

    @Id
    private String id;

    // Note: ISO-8601 representation.
    private String timeOfRequest;

    private String mostFrequentWord;

    private long averageParagraphSize;

    private long averageParagraphProcessingTimeNs;

    private long averageParagraphProcessingTimeMs;

    private long totalProcessingTimeNs;

    private long totalProcessingTimeMs;

}
