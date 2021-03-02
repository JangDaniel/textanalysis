package com.insutil.textanalysis.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
public class SttSentences extends BaseModel {

    @Id
    private Long id;

    private Long sttId;
    private String unitSentence;
    private String morpheme;
    private Long scriptDetailId;
    private BigDecimal similarityScore;
}
