package com.chriniko.job.assignment.resource;

import com.chriniko.job.assignment.domain.TextProcessingEntry;
import com.chriniko.job.assignment.repository.TextProcessingEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("job-assignment/history")
public class TextProcessingHistoryResource {

    private static final Sort SORT_BY_TIME_OF_REQUEST_DESC = Sort.by(Sort.Order.desc("timeOfRequest"));

    private final TextProcessingEntryRepository textProcessingEntryRepository;

    @Autowired
    public TextProcessingHistoryResource(TextProcessingEntryRepository textProcessingEntryRepository) {
        this.textProcessingEntryRepository = textProcessingEntryRepository;
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<Page<TextProcessingEntry>> entries(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, SORT_BY_TIME_OF_REQUEST_DESC);
        Page<TextProcessingEntry> page = textProcessingEntryRepository.findAll(pageable);

        return ResponseEntity.ok(page);
    }

}
