package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@With
@Data
@Builder
@Table(value = "t_ta_stt_sentences")
public class SttSentences extends BaseModel {

    @Id
    private Long id;

    private Long sttId;
    private String unitSentence;
    private String morpheme;
    private Long scriptDetailId;
    private BigDecimal similarityScore;
}
