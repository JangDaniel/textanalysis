package com.insutil.textanalysis.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static DateTimeFormatter formatterYYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String getNowCallDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(formatterYYYYMMDD);
    }

}
