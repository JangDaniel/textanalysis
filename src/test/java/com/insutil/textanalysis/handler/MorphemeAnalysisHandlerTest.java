package com.insutil.textanalysis.handler;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MorphemeAnalysisHandlerTest {

    @Autowired
    MorphemeAnalysisHandler morphemeAnalysisHandler;

    @Test
    public void analysisTest() {
        morphemeAnalysisHandler.analysis(Arrays.array(1, 2, 3, 4, 5));
    }
}
