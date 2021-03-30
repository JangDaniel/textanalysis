package com.insutil.textanalysis.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(value = "t_ta_allocation_break")
@ToString
@Getter
@Setter
public class AllocationBreak {
	@Id
	private Long id;
	private Long evaluatorId;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer breakValue;
	private Long registUser;
	private Long updateUser;
	private LocalDateTime registDate;
	private LocalDateTime updateDate;
	private Boolean enabled;

	public AllocationBreak update(AllocationBreak target) {
		if (target.startDate != null) this.startDate = target.startDate;
		if (target.endDate != null) this.endDate = target.endDate;
		if (target.breakValue != null) this.breakValue = target.breakValue;
		if (target.updateUser != null) this.updateUser = target.updateUser;
		if (target.enabled != null) this.enabled = target.enabled;
		return this;
	}
}
