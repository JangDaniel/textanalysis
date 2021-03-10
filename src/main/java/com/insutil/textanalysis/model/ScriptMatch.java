package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_ta_stt_script_match")
@ToString
@Getter
@Setter
@Builder
//@EqualsAndHashCode(of = {"id"})
public class ScriptMatch {
	@Id
	private Long id;
	private Long criterionEvaluationId;

	private Long scriptDetailId;
	private Long sttSentenceId;
	private Integer matchRate;
	private Long registUser;
	private Long updateUser;
	private LocalDateTime registDate;
	private LocalDateTime updateDate;
	private Boolean enabled;

	@Transient
	@With
	private ScriptDetail scriptDetail;

	@Transient
	@With
	private SttSentences sttSentence;

	public ScriptMatch update(ScriptMatch target) {
		if (target.criterionEvaluationId != null) this.criterionEvaluationId = target.criterionEvaluationId;
		if (target.scriptDetailId != null) this.scriptDetailId = target.scriptDetailId;
		if (target.sttSentenceId != null) this.sttSentenceId = target.sttSentenceId;
		if (target.matchRate != null) this.matchRate = target.matchRate;
		if (target.updateUser != null) this.updateUser = target.updateUser;
		if (target.enabled != null) this.enabled = target.enabled;

		return this;
	}
}

/* DDL
CREATE TABLE `t_ta_stt_script_match` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `criterion_evaluation_id` int(11) NOT NULL,
  `script_detail_id` int(11) NOT NULL,
  `stt_sentence_id` bigint(20) NOT NULL,
  `match_rate` int(3) NOT NULL,
  `regist_user` int(11) DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  `regist_date` datetime DEFAULT current_timestamp(),
  `update_date` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `enabled` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `t_ta_stt_script_match_criterion_evaluation_id_FK` (`criterion_evaluation_id`),
  KEY `t_ta_stt_script_match_script_detail_id_FK` (`script_detail_id`),
  KEY `t_ta_stt_script_match_stt_sentence_id_FK` (`stt_sentence_id`),
  CONSTRAINT `t_ta_stt_script_match_criterion_evaluation_id_FK` FOREIGN KEY (`criterion_evaluation_id`) REFERENCES `t_ta_stt_criterion_evaluation` (`id`),
  CONSTRAINT `t_ta_stt_script_match_script_detail_id_FK` FOREIGN KEY (`script_detail_id`) REFERENCES `t_ta_script_detail` (`id`),
  CONSTRAINT `t_ta_stt_script_match_stt_sentence_id_FK` FOREIGN KEY (`stt_sentence_id`) REFERENCES `t_ta_stt_sentences` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
*/