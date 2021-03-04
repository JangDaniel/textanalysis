package com.insutil.textanalysis.batch;

import com.insutil.textanalysis.analysis.SentenceManager;
import com.insutil.textanalysis.common.util.DateTimeUtil;
import com.insutil.textanalysis.model.STTContents;
import com.insutil.textanalysis.model.SttSentences;
import com.insutil.textanalysis.repository.SttContentsRepository;
import com.insutil.textanalysis.repository.SttSentencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:analysis.properties")
@Component
public class ScheduleTask {

    // 0 : 문장 분리 가능, 1: 문장 분리 작업 중
    // 2 : 형태소 분석 가능, 3: 형태소 분석 작업 중
    // 4 : 문장 분석 가능, 5: 문장 분석 작업 중
    private AtomicInteger atomicInteger = new AtomicInteger();

    private final SentenceManager sentenceManager;

    private final SttContentsRepository sttContentsRepository;

    private final SttSentencesRepository sttSentencesRepository;

    private final static int cores = Runtime.getRuntime().availableProcessors() / 2;

    // 수동 모드 1) 문장 분리 T_TA_STT_CONTENTS => T_TA_STT_SENTENCES
    @Scheduled(fixedDelayString = "${analysis.fixed.delay}")
    public void preparedAnalysis() {
        if (!atomicInteger.compareAndSet(0, 1))
            return;
        log.info("preparedAnalysis()");
        Flux<STTContents> sttContentsFlux = sttContentsRepository.findSTTContentsByCallDateAndStateCode(DateTimeUtil.getNowCallDate(), 33);

        sttContentsFlux
                .parallel()
                .runOn(Schedulers.parallel())
                .doOnNext(data -> onGoingMorphemeAnalysis(data))
                .map(data -> makeSentencesData(data))
                .sequential()
                .publishOn(Schedulers.single())
                .subscribe(data -> {
                    data.stream().forEach(d -> {
                        log.info("saving... {}", d);
                        try {
                            sttSentencesRepository.save(d).subscribe();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                });

        log.info("complete...");
        // T_TA_STT_CONTENTS 테이블에서 분석 대상인 데이터 추출해서, T_TA_STT_SENTENCES에 입력
//        atomicInteger.set(0);
    }

    private void onGoingMorphemeAnalysis(STTContents sttContents) {
        if(sttContents.getSttText().length() > 10) {
            sttContentsRepository.updateStateCode(35, sttContents.getId())
                    .subscribe();
        } else {
            sttContentsRepository.updateStateCode(39, sttContents.getId())
                    .subscribe();
        }
    }

    private List<SttSentences> makeSentencesData(STTContents data) {
        log.info("working ... {}", data.getId());

        List<SttSentences> sttSentencesList = new ArrayList<>();

        List<String> agentSentences = sentenceManager.extractAgentSentence(data.getSttText());
        Map<String, String> result = agentSentences.stream()
                .collect(Collectors.toMap(s -> s,
                        s -> sentenceManager.extractNoun(s),
                        (existingKey, newKey) -> existingKey));

        for (Map.Entry<String, String> elem : result.entrySet()) {
            SttSentences sttSentences = new SttSentences();
            sttSentences.setSttId(data.getId());
            sttSentences.setUnitSentence(elem.getKey());
            sttSentences.setMorpheme(elem.getValue());
            sttSentencesList.add(sttSentences);
            log.info("{}, {}", elem.getKey(), elem.getValue());
        }
        return sttSentencesList;
    }



    // 수동 모드 2) 형태소 분석

    // 수동 모드 3) 유사도 분석
}
