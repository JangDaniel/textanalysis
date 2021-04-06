package com.insutil.textanalysis.model.dto;

import lombok.*;

import java.time.LocalDate;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationSearchParam {
	private LocalDate fromDate;
	private LocalDate toDate;
	private int offset;
	private int limit;
}
