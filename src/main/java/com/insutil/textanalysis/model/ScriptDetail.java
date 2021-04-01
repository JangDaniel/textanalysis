package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_ta_script_detail")
@ToString
@Getter
@Setter
@Builder
//@EqualsAndHashCode(of = {"id"})
public class ScriptDetail {
	@Id
	private Long id;
	private Long criterionId;

	private Integer sort;
	private String script;

	// 변경 할 객체 정보가 있으면 변경된 대로, 객체가 없으면 script 원문 그대로 복사
	@Transient
	private String postObjectProcessingScript;

	private String morpheme;
	private Integer score;
	private Long registUser;
	private Long updateUser;
	private LocalDateTime registDate;
	private LocalDateTime updateDate;
	private Boolean enabled;


	@Transient
	private String objectReplacedScript;

	@Transient
	@With
	private List<ScriptDetailMainWord> mainWords;

	@Transient
	@With
	private Flux<ScriptDetailMainWord> mainWordsFlux;



	public ScriptDetail update(ScriptDetail scriptDetail) {
		if (scriptDetail.criterionId != null) this.criterionId = scriptDetail.criterionId;
		if (scriptDetail.sort != null) this.sort = scriptDetail.sort;
		if (scriptDetail.script != null) this.script = scriptDetail.script;
		if (scriptDetail.morpheme != null) this.morpheme = scriptDetail.morpheme;
		if (scriptDetail.updateUser != null) this.updateUser = scriptDetail.updateUser;
		if (scriptDetail.score != null) this.score = scriptDetail.score;
		if (scriptDetail.enabled != null) this.enabled = scriptDetail.enabled;
		if (scriptDetail.mainWords != null) this.mainWords = scriptDetail.mainWords;

		return this;
	}
}

/* DDL
CREATE TABLE `t_ta_script_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `criterion_id` int(11) NOT NULL,
  `script` mediumtext DEFAULT NULL,
  `sort` int(2) DEFAULT 1,
  `score` int(2) DEFAULT NULL,
  `regist_user` int(11) NOT NULL,
  `update_user` int(11) DEFAULT NULL,
  `regist_date` datetime DEFAULT current_timestamp(),
  `update_date` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `t_ta_script_detail_criterion_id_FK` (`criterion_id`),
  CONSTRAINT `t_ta_script_detail_criterion_id_FK` FOREIGN KEY (`criterion_id`) REFERENCES `t_ta_script_criteria` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8
*/