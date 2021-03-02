package com.insutil.textanalysis.analysis;

import com.insutil.textanalysis.common.analysis.PosTagging;
import com.insutil.textanalysis.model.STTContents;
import com.insutil.textanalysis.repository.STTContentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SentenceManager {

    private final STTContentsRepository sttContentsRepository;
    private final PosTagging posTagging;

    public List<String> extractAgentSentence(final String contents) throws InvalidParameterException {
        if (!StringUtils.hasLength(contents))
            throw new InvalidParameterException();

        return Arrays.asList(StringUtils.tokenizeToStringArray(contents, "\n"))
                .stream()
                .filter(s -> s.startsWith("A"))
                .map(s -> s.replaceFirst("^A:\\[‡\\d+‡\\]", ""))
                .collect(Collectors.toList());

    }

    public Map<String, List<String>> extractNoun(final String sentence) throws InvalidParameterException {
        if (!StringUtils.hasLength(sentence))
            throw new InvalidParameterException();
        return Map.of(sentence, posTagging.extractNounTag(sentence));
    }



}
