package com.insutil.textanalysis.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(value = "t_qa_holiday")
@Setter
@Getter
public class Holiday {
	private Long id;
	private String title;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDate afterWorkingDate;
	private Boolean enabled;
}
