package com.insutil.textanalysis.model;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllocatedCount {
	private Long evaluatorId;
	private Long insuranceType;
	private Integer count;
}
