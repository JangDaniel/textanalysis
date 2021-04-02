package com.insutil.textanalysis.model;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllocationCount {
	private Long evaluatorId;
	private Long insuranceType;
	private Integer count;
}
