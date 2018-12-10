package com.chriniko.job.assignment.repository;

import com.chriniko.job.assignment.domain.TextProcessingEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TextProcessingEntryRepository extends MongoRepository<TextProcessingEntry, String> {
}
