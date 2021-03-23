package com.insutil.textanalysis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class CriterionEvaluationSummary {
	private Long sttId;
	private Long sttEvaluationId;
	private Long parentCriterionId;
	private Long criterionId; // root criterion
	private String criterionName;
	private Integer criterionSort; // just root criterion's sort
	private Long criterionEvaluationId;
	private Integer score;
	private String opinion;

	// leaf criterion 의 갯수만큼 CriterionEvaluationDetail 필요
	// 한개의 criterion 에 n개의 script detail 이 있는 경우 t_ta_stt_script_match 테이블의 similarity score 가 있는 script detail 만을 가진다
	// t_ta_stt_script_match 테이블에 criterion 의 script details 가 한개도 없는 경우 첫번째 script detail 의 mapped script 사용
	// TODO: 즉, TA 엔진에서는 stt 와 matching 되지 않는 script 도 객체와 mapping 해서 t_ta_stt_script_match 테이블에 입력해야 한다.
//  private Long rootScriptDetailId; this.criterionId 와 같다
//	private Long scriptDetailId;
//	private String script; // script detail's original script
//	private String mappedScript; // object mapped script
//	private String unitSentence;
//	private Float similarityScore;

	private List<CriterionEvaluationDetail> criterionEvaluationDetails;
}
