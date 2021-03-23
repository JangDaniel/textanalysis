package com.insutil.textanalysis.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CriterionEvaluationDetail {
	private Long scriptDetailId;
	private String script; // script detail's original script
	private String mappedScript; // object mapped script
	private String unitSentence;
	private Float similarityScore;
	private String criterionName;
	private Integer criterionSort;
	private Integer baseScore;

}
