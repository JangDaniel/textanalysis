package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_ta_stt_criterion_evaluation")
@ToString
@Getter
@Setter
@Builder
//@EqualsAndHashCode(of = {"id"})
public class CriterionEvaluation {
	@Id
	private Long id;
	private Long sttEvaluationId;

	private Long criterionId;
	private Integer score;
	private String opinion;
	private Long registUser;
	private Long updateUser;
	private LocalDateTime registDate;
	private LocalDateTime updateDate;
	private Boolean enabled;

	@Transient
	@With
	private ScriptCriterion criterion;

	@Transient
	@With
	private List<ScriptMatch> scriptMatches;

	public CriterionEvaluation update(CriterionEvaluation target) {
		if (target.sttEvaluationId != null) this.sttEvaluationId = target.sttEvaluationId;
		if (target.criterionId != null) this.criterionId = target.criterionId;
		if (target.score != null) this.score = target.score;
		if (target.opinion != null) this.opinion = target.opinion;
		if (target.updateUser != null) this.updateUser = target.updateUser;
		if (target.enabled != null) this.enabled = target.enabled;
		if (target.scriptMatches != null) this.scriptMatches = target.scriptMatches;

		return this;
	}
}

/* DDL
CREATE TABLE `t_ta_stt_criterion_evaluation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stt_evaluation_id` int(11) NOT NULL,
  `criterion_id` int(11) NOT NULL,
  `score` int(3) DEFAULT NULL,
  `opinion` varchar(256) DEFAULT NULL,
  `regist_user` int(11) DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  `regist_date` datetime DEFAULT current_timestamp(),
  `update_date` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `enabled` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `t_ta_stt_script_evaluation_call_evaluation_id_FK` (`call_evaluation_id`),
  KEY `t_ta_stt_script_evaluation_criterion_id_FK` (`criterion_id`),
  CONSTRAINT `t_ta_stt_script_evaluation_call_evaluation_id_FK` FOREIGN KEY (`call_evaluation_id`) REFERENCES `t_ta_call_evaluation` (`id`),
  CONSTRAINT `t_ta_stt_script_evaluation_criterion_id_FK` FOREIGN KEY (`criterion_id`) REFERENCES `t_ta_script_criteria` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
*/