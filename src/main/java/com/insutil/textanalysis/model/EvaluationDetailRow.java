package com.insutil.textanalysis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EvaluationDetailRow {
	private Long criterionId;
	private Long scriptDetailId;
	private Long sttSentenceId;
	private String unitSentence;
	private String mappedScript;
	private Float similarityScore;
	private ScriptDetail scriptDetail;
	private ScriptCriterion scriptCriterion;
	private ScriptCriterion rootScriptCriterion;
}
