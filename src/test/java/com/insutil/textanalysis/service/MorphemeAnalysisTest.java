package com.insutil.textanalysis.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MorphemeAnalysisTest {

    @Autowired
    MorphemeAnalysis morphemeAnalysis;

    @Test
    public void analysisMorphemeTest() {
//        System.out.println(morphemeAnalysis.analysisMorpheme("오늘 보험사에 승환계약하러 휴면계좌 가지고 장석봉과 같이 보험사에 갑니다."));
        System.out.println(morphemeAnalysis.analysisMorpheme("증권 약관 은 지금 메일 쪽 으로 바로 문자 함께 넣어드릴 건데 오늘 이 전화 로 가실 거예요"));
    }
}