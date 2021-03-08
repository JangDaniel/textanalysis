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
    private String callDate;
    private String unitSentence;
    private String morpheme;
    private Long scriptDetailId;
    private BigDecimal similarityScore;
}

/*
CREATE TABLE `t_ta_stt_sentences` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `stt_id` int(11) NOT NULL,
  `call_date` varchar(8) NOT NULL,
  `unit_sentence` text NOT NULL,
  `morpheme` text NOT NULL,
  `script_detail_id` int(11) DEFAULT NULL,
  `similarity_score` float DEFAULT NULL,
  `regist_user` int(11) DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  `regist_date` datetime DEFAULT current_timestamp(),
  `update_date` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `t_ta_stt_sentences_call_date_IDX` (`call_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8
 */