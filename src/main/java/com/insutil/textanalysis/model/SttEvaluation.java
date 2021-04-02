package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_ta_stt_evaluation")
@ToString
@Getter
@Setter
@Builder
//@EqualsAndHashCode(of = {"id"})
public class SttEvaluation {
	@Id
	private Long id;
	private Long sttId;
	private Long insuranceType;

	private Long stateId;
	private Long resultId;
	private Long evaluatorId;
	private String opinion;
	private Long registUser;
	private Long updateUser;
	private LocalDateTime registDate;
	private LocalDateTime updateDate;
	private Boolean enabled;

	@Transient
	@With
	private STTContents stt;

	@Transient
	@With
	private User evaluator;

	@Transient
	@With
	private List<CriterionEvaluation> criterionEvaluations; // for save or update

	@Transient
	@With
	private List<ScriptMatch> scriptMatches;

	@Transient
	@With
	private List<CriterionEvaluationSummary> criterionEvaluationSummaries; // for read

	public SttEvaluation update(SttEvaluation target) {
		if (target.sttId != null) this.sttId = target.sttId;
		if (target.insuranceType != null) this.insuranceType = target.insuranceType;
		if (target.stateId != null) this.stateId = target.stateId;
		if (target.resultId != null) this.resultId = target.resultId;
		if (target.evaluatorId != null) this.evaluatorId = target.evaluatorId;
		if (target.opinion != null) this.opinion = target.opinion;
		if (target.updateUser != null) this.updateUser = target.updateUser;
		if (target.enabled != null) this.enabled = target.enabled;
		if (target.criterionEvaluations != null) this.criterionEvaluations = target.criterionEvaluations;

		return this;
	}
}

/* DDL
CREATE TABLE `t_ta_call_evaluation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stt_id` int(11) NOT NULL,
  `state_id` int(11) NOT NULL COMMENT '평가전/평가완료',
  `result_id` int(11) DEFAULT NULL COMMENT '완전판매/불완전판매',
  `evaluator_id` int(11) NOT NULL COMMENT '평가사',
  `opinion` varchar(256) DEFAULT NULL,
  `regist_user` int(11) DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  `regist_date` datetime DEFAULT current_timestamp(),
  `update_date` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `enabled` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `t_ta_stt_call_evaluation_stt_id_FK` (`stt_id`),
  CONSTRAINT `t_ta_stt_call_evaluation_stt_id_FK` FOREIGN KEY (`stt_id`) REFERENCES `t_ta_stt_contents` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
*/