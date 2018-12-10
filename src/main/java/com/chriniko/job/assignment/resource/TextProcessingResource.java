package com.chriniko.job.assignment.resource;

import com.chriniko.job.assignment.dto.TextProcessingResult;
import com.chriniko.job.assignment.service.TextProcessingService;
import com.chriniko.job.assignment.validator.TextProcessingValidator;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("job-assignment")
public class TextProcessingResource {

    private final TextProcessingValidator textProcessingValidator;
    private final TextProcessingService textProcessingService;

    @Autowired
    public TextProcessingResource(TextProcessingService textProcessingService,
                                  TextProcessingValidator textProcessingValidator) {
        this.textProcessingService = textProcessingService;
        this.textProcessingValidator = textProcessingValidator;
    }

    @RequestMapping(
            path = "text",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public HttpEntity<TextProcessingResult> calculate(@RequestParam(name = "p_start") int paragraphStart,
                                                      @RequestParam(name = "p_end") int paragraphEnd,
                                                      @RequestParam(name = "w_count_min") int minNumberOfWordsPerParagraph,
                                                      @RequestParam(name = "w_count_max") int maxNumberOfWordsPerParagraph) {

        textProcessingValidator.test(
                Pair.with("p_start", paragraphStart),
                Pair.with("p_end", paragraphEnd),
                Pair.with("w_count_min", minNumberOfWordsPerParagraph),
                Pair.with("w_count_max", maxNumberOfWordsPerParagraph)
        );

        TextProcessingResult result
                = textProcessingService.process(paragraphStart,
                                                paragraphEnd,
                                                minNumberOfWordsPerParagraph,
                                                maxNumberOfWordsPerParagraph);

        return ResponseEntity.ok(result);
    }

}
