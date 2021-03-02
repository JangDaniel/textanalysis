package com.insutil.textanalysis.batch;

import com.insutil.textanalysis.analysis.SentenceManager;
import com.insutil.textanalysis.common.util.DateTimeUtil;
import com.insutil.textanalysis.model.STTContents;
import com.insutil.textanalysis.model.SttSentences;
import com.insutil.textanalysis.repository.STTContentsRepository;
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

    private final STTContentsRepository sttContentsRepository;

    // 수동 모드 1) 문장 분리 T_TA_STT_CONTENTS => T_TA_STT_SENTENCES
    @Scheduled(fixedDelayString = "${analysis.fixed.delay}")
    public void preparedAnalysis() {
        if(!atomicInteger.compareAndSet(0, 1))
            return;
        log.info("preparedAnalysis()");
        Flux<STTContents> sttContentsFlux = sttContentsRepository.findSTTContentsByCallDateAndStateCode(DateTimeUtil.getNowCallDate(), 33);

        String result = sttContentsFlux
                .parallel(4)
                .runOn(Schedulers.newParallel("analysis", 4))
                .map(data -> {
                    makeSentencesData(data);
                    return "";
                })
                .sequential()
                .blockLast();
        // T_TA_STT_CONTENTS 테이블에서 분석 대상인 데이터 추출해서, T_TA_STT_SENTENCES에 입력
    }

    private void makeSentencesData(STTContents data) {
        List<SttSentences> sttSentencesList = new ArrayList<>();
        List<String> agentSentences = sentenceManager.extractAgentSentence(data.getSttText());
        Map<String, String> result = agentSentences.stream()
                .collect(Collectors.toMap(s -> s,
                        s -> sentenceManager.extractNoun(s),
                        (existingKey, newKey) -> existingKey));

    }

    // 수동 모드 2) 형태소 분석

    // 수동 모드 3) 유사도 분석
}
