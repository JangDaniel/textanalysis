package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;


@Table(value = "t_ta_script_criteria")
@ToString
@Getter
@Setter
//@EqualsAndHashCode(of = {"id", "departmentDepth", "departmentName"})
@AllArgsConstructor
@NoArgsConstructor
public class ScriptCriterion {
	@Id
	private Long id;
	private Long productId;
	private String name;
	private Long parentId;
	private Integer depth;
	private Integer sort;
	private Long registUser;
	private Long updateUser;
	private LocalDateTime registDate;
	private LocalDateTime updateDate;
	private Boolean enabled;

	@Transient
	@With
	private List<ScriptDetail> scriptDetails;

	@Transient
	@With
	private Integer childCount;

	public ScriptCriterion update(ScriptCriterion scriptCriterion) {
		if (scriptCriterion.productId != null) this.productId = scriptCriterion.productId;
		if (scriptCriterion.name != null) this.name = scriptCriterion.name;
		if (scriptCriterion.parentId != null) this.parentId = scriptCriterion.parentId;
		if (scriptCriterion.depth != null) this.depth = scriptCriterion.depth;
		if (scriptCriterion.sort != null) this.sort = scriptCriterion.sort;
		if (scriptCriterion.updateUser != null) this.updateUser = scriptCriterion.updateUser;
		if (scriptCriterion.enabled != null) this.enabled = scriptCriterion.enabled;
		return this;
	}
}

/* DDL
CREATE TABLE `t_ta_script_criteria` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `name` varchar(512) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `depth` int(1) DEFAULT 1,
  `sort` int(2) DEFAULT 0,
  `regist_user` int(11) NOT NULL,
  `update_user` int(11) DEFAULT NULL,
  `regist_date` datetime DEFAULT current_timestamp(),
  `update_date` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `t_ta_script_criteria_product_id_FK` (`product_id`),
  CONSTRAINT `t_ta_script_criteria_product_id_FK` FOREIGN KEY (`product_id`) REFERENCES `t_qa_product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
*/