package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * TODO: 보종별 value(motorValue, longTermValue, commonValue) 추후 DB 정규화가 필요할 수 있다.
 * TODO: 보종은 현재는 t_qa_product_category 테이블의 root 요소들이다.
 * TODO: 정규화를 한다면 t_ins_code 의 code_id 가 INSURANCE_TYPE 을 t_qa_product_category 에 넣어야 한다.
 */
@Table(value = "t_ta_stt_evaluation_allocation")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Allocation {
	@Id
	private Long id;
	private LocalDate date;
	private Long evaluatorId;
	private Long insuranceType;
	private Integer count;
	private Long registUser;
	private Long updateUser;
	private LocalDateTime registDate;
	private LocalDateTime updateDate;
	private Boolean enabled;

	@Transient
	@With
	private User evaluator;

	@Transient
	@With
	private Code insuranceTypeCode;

	public Allocation update(Allocation target) {
		if (target.date != null) this.date = target.date;
		if (target.evaluatorId != null) this.evaluatorId = target.evaluatorId;
		if (target.insuranceType != null) this.insuranceType = target.insuranceType;
		if (target.count != null) this.count = target.count;
		if (target.updateUser != null) this.updateUser = target.updateUser;
		if (target.enabled != null) this.enabled = target.enabled;
		return this;
	}
}

/*
CREATE TABLE `t_ta_stt_evaluation_allocation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year_month` date NOT NULL,
  `evaluator_id` int(11) NOT NULL,
  `motor_count` int(3) DEFAULT NULL,
  `longterm_count` int(3) DEFAULT NULL,
  `common_count` int(3) DEFAULT NULL,
  `regist_user` int(11) DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  `regist_date` datetime DEFAULT current_timestamp(),
  `update_date` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `enabled` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
 */