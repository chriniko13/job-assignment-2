package com.chriniko.job.assignment.validator;

import com.chriniko.job.assignment.error.ValidationException;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

@Component
public class TextProcessingValidator {

    private static final int PARAGRAPH_COUNT_THRESHOLD = 200;
    private static final int MAX_WORDS_PER_PARAGRAPH_COUNT_THRESHOLD = 500;

    public void test(Pair<String, Integer> paragraphStart,
                     Pair<String, Integer> paragraphEnd,
                     Pair<String, Integer> minNumberOfWordsPerParagraph,
                     Pair<String, Integer> maxNumberOfWordsPerParagraph) {

        if (paragraphEnd.getValue1() > PARAGRAPH_COUNT_THRESHOLD) {
            throw new ValidationException("paragraph count threshold("
                    + PARAGRAPH_COUNT_THRESHOLD
                    + ") exceeded");
        }

        if (maxNumberOfWordsPerParagraph.getValue1() > MAX_WORDS_PER_PARAGRAPH_COUNT_THRESHOLD) {
            throw new ValidationException("max words per paragraph count threshold("
                    + MAX_WORDS_PER_PARAGRAPH_COUNT_THRESHOLD
                    + ") exceeded");

        }

        if (paragraphStart.getValue1() > paragraphEnd.getValue1()) {

            String errorMessage = String.format("%s is bigger than %s", paragraphStart.getValue0(), paragraphEnd.getValue0());
            throw new ValidationException(errorMessage);
        }

        if (minNumberOfWordsPerParagraph.getValue1() > maxNumberOfWordsPerParagraph.getValue1()) {

            String errorMessage = String.format("%s is bigger than %s", minNumberOfWordsPerParagraph.getValue0(), maxNumberOfWordsPerParagraph.getValue0());
            throw new ValidationException(errorMessage);
        }
    }

}
