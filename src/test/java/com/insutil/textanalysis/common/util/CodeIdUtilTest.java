package com.insutil.textanalysis.common.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CodeIdUtilTest {

    @Autowired
    CodeIdUtil codeIdUtil;

    @Test
    public void getCodeIdByCodeIdTest() {
        System.out.println(codeIdUtil.getCodeIdByCodeId("AM"));
    }
}