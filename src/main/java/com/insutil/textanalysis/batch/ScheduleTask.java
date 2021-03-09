package com.insutil.textanalysis.batch;

import com.insutil.textanalysis.common.util.CodeIdUtil;
import com.insutil.textanalysis.common.util.DateTimeUtil;
import com.insutil.textanalysis.model.STTContents;
import com.insutil.textanalysis.repository.SttContentsRepository;
import com.insutil.textanalysis.service.MorphemeAnalysis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:analysis.properties")
@Component
@ConditionalOnProperty(name = "jobs.enabled", matchIfMissing = true, havingValue = "true")
public class ScheduleTask {

    // 0 : 문장 분리 가능, 1: 문장 분리 작업 중
    // 2 : 형태소 분석 가능, 3: 형태소 분석 작업 중
    // 4 : 문장 분석 가능, 5: 문장 분석 작업 중
    private AtomicInteger atomicInteger = new AtomicInteger();

    private final SttContentsRepository sttContentsRepository;


    private final MorphemeAnalysis morphemeAnalysis;

    private final CodeIdUtil codeIdUtil;

    private final static int cores = Runtime.getRuntime().availableProcessors() / 2;

    private long codeWithAfterMorphemeAnalysis;     // 형태소 분석 후 상태 코드 값
    private long codeWithExceptMorphemeAnalysis;    // 형태소 분석 제외 상태 코드 값
    @PostConstruct
    public void init() {
        codeWithAfterMorphemeAnalysis = codeIdUtil.getCodeIdByCodeId("AM");
        codeWithExceptMorphemeAnalysis = codeIdUtil.getCodeIdByCodeId("EM");
    }

    // 수동 모드 1) 문장 분리 T_TA_STT_CONTENTS => T_TA_STT_SENTENCES
    @Scheduled(fixedDelayString = "${analysis.fixed.delay}")
    public void preparedAnalysis() {
        if (!atomicInteger.compareAndSet(0, 1))
            return;

        // T_TA_STT_CONTENTS 테이블에서 분석 대상인 데이터 추출해서, T_TA_STT_SENTENCES에 입력
        morphemeAnalysis.getMorphemeAnalysisTargetWithCallDate(DateTimeUtil.getNowCallDate())
                .parallel()
                .runOn(Schedulers.parallel())
                .doOnNext(this::onGoingMorphemeAnalysis)    // 상태 전환
                .map(morphemeAnalysis::analysisMorpheme) // 문장 분리 및 형태소 분석
                .sequential()
                .publishOn(Schedulers.single())
                .subscribe(morphemeAnalysis::saveSentenceData); // t_ta_stt_sentences 형태소 분석 결과 입력

        log.info("complete...");

        atomicInteger.set(0);
    }

    private void onGoingMorphemeAnalysis(STTContents sttContents) {
        long code = sttContents.getSttText().length() > 20 ?
                codeWithAfterMorphemeAnalysis : codeWithExceptMorphemeAnalysis;
        sttContentsRepository.updateStateCode(code, sttContents.getId()).subscribe();
    }

    public void stopJobs() {
        atomicInteger.set(1);
    }

    public void resumeJobs() {
        atomicInteger.set(0);
    }

    // 수동 모드 2) 형태소 분석

    // 수동 모드 3) 유사도 분석
}
