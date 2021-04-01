package com.insutil.textanalysis.service;

import com.insutil.textanalysis.common.model.PosPair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class MorphemeAnalysisTest {

    @Autowired
    MorphemeAnalysis morphemeAnalysis;

    @Test
    public void analysisMorphemeTest() {
//        System.out.println(morphemeAnalysis.analysisMorpheme("오늘 보험사에 승환계약하러 휴면계좌 가지고 장석봉과 같이 보험사에 갑니다."));
        List<PosPair> posPairs = morphemeAnalysis.extractSentenceAndAnalysisMorpheme("고객님께서지금들고계시는그에스엠세븐사고도일공오일년십이");
        System.out.println(morphemeAnalysis.extractSentenceAndAnalysisMorpheme("근데그게이제삼년을따라가니까 아직삼년안에갖고있다고--사고는"));
    }

    @Test
    public void preparedAnalysisWithDateTest() {
        assertThat(morphemeAnalysis.preparedAnalysisWithDate("20210309")).isEqualTo(0);
    }

    @Test
    public void analysisMorphemeWithSpecificContractNoTest() {
        morphemeAnalysis.analysisMorphemeWithSpecificContractNo("A19020176859")
                .as(StepVerifier::create)
                .thenConsumeWhile(data -> data.getId() != null)
                .verifyComplete();

    }

    // T_TA_STT_SENTENCES 의 해당 조건의 STT에 대한 유사도 점수 계산
    @Test
    public void analysisSimilarityTest() {
        morphemeAnalysis.analysisSimilarity("20210308", "A19020176859")
                .as(StepVerifier::create)
                .thenConsumeWhile(data -> data.getId() != null)
                .verifyComplete();

    }

    @Test
    public void extractContractObjectTest() {
        morphemeAnalysis.extractContractObject("20210308", "A19020174759");
    }

    @Test
    public void checkWordSimilarityTest() {
//        morphemeAnalysis.checkWordSimilarity("확인,고객님,예,가입,팩스,로", "확인,고객님");
    }
}