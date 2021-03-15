package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@With
@Getter
@Setter
@Builder
@Table(value = "t_ta_stt_sentences")
public class SttSentences extends BaseModel {

    @Id
    private Long id;

    private Long sttId;
    private String contractNo;
    private String callDate;
    private String unitSentence;
    private String morpheme;
}

/*
CREATE TABLE `t_ta_stt_sentences` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `stt_id` int(11) NOT NULL,
  `contract_no` varchar(12) NOT NULL,
  `call_date` varchar(8) NOT NULL,
  `unit_sentence` text NOT NULL,
  `morpheme` text NOT NULL,
  `regist_user` int(11) DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  `regist_date` datetime DEFAULT current_timestamp(),
  `update_date` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `t_ta_stt_sentences_call_date_IDX` (`call_date`) USING BTREE,
  KEY `t_ta_stt_sentences_contract_no_IDX` (`contract_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8
 */