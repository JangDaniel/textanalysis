package com.insutil.textanalysis.service;

import com.insutil.textanalysis.analysis.SentenceManager;
import com.insutil.textanalysis.common.analysis.PosTagging;
import com.insutil.textanalysis.common.model.PosPair;
import com.insutil.textanalysis.model.STTContents;
import com.insutil.textanalysis.model.SttSentences;
import com.insutil.textanalysis.repository.CodeRepository;
import com.insutil.textanalysis.repository.SttContentsRepository;
import com.insutil.textanalysis.repository.SttSentencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MorphemeAnalysis {
    private final SentenceManager sentenceManager;

    private final PosTagging posTagging;

    private final SttContentsRepository sttContentsRepository;

    private final CodeRepository codeRepository;

    private final SttSentencesRepository sttSentencesRepository;


    private long codeWithBeforeMorphemeAnalysis;

    @PostConstruct
    public void init() {
        codeRepository.findEnabledCodeByCodeId("BM")
                .doOnNext(t -> {
                    codeWithBeforeMorphemeAnalysis = t.getId();
                }).subscribe();
    }

    public List<PosPair> analysisMorpheme(String sourceText) {
        return posTagging.tagPos(sourceText);
    }

    public Flux<STTContents> getMorphemeAnalysisTargetWithCallDate(final String callDate) {
        return sttContentsRepository
                .findSTTContentsByCallDateAndStateCode(callDate, codeWithBeforeMorphemeAnalysis);
    }


    public List<SttSentences> analysisMorpheme(STTContents data) {
        log.info("working ... {}", data.getId());

        List<SttSentences> sttSentencesList = new ArrayList<>();

        try {
            List<String> agentSentences = sentenceManager.extractAgentSentence(data.getSttText());
            Map<String, String> result = agentSentences.stream()
                    .collect(Collectors.toMap(s -> s,
                            s -> sentenceManager.extractNoun(s),
                            (existingKey, newKey) -> existingKey));
            for (Map.Entry<String, String> elem : result.entrySet()) {
                SttSentences sttSentences = new SttSentences();
                sttSentences.setSttId(data.getId());
                sttSentences.setCallDate(data.getCallDate());
                sttSentences.setUnitSentence(elem.getKey());
                sttSentences.setMorpheme(elem.getValue());
                sttSentencesList.add(sttSentences);
                log.info("{}, {}", elem.getKey(), elem.getValue());
            }
        } catch(InvalidParameterException e) {
            log.error("occur to InvalidParameterException {}", data);
        }

        return sttSentencesList;
    }

    public Flux<SttSentences> saveSentenceData(final List<SttSentences> sttSentences) {
        return sttSentencesRepository.saveAll(sttSentences);
    }

    public int preparedAnalysisWithDate(final String callDate) {
        // t_ta_stt_sentences callDate 건은 삭제
        log.info("is going to delete result data in t_ta_stt_sentences with specific date = {}", callDate);
        return sttSentencesRepository.deleteSttSentencesByCallDate(callDate)
                .subscribeOn(Schedulers.parallel()).block();
    }

    public Flux<SttSentences> analysisMorphemeWithSpecificContractNo(final String contractNo) {
        return sttContentsRepository.findSTTContentsByContractNo(contractNo)
                .flatMap(data -> Flux.just(analysisMorpheme(data)))
                .flatMap(data -> saveSentenceData(data));
//                .reduce((b, a) -> a && b);
    }
}
