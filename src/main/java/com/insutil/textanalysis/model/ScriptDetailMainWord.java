package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_ta_script_detail_main_word")
@ToString
@Getter
@Setter
@Builder
public class ScriptDetailMainWord {
	@Id
	private Long id;
	private Long scriptDetailId;
	private String word;
	private Integer weight;
	private Long registUser;
	private Long updateUser;
	private LocalDateTime registDate;
	private LocalDateTime updateDate;
	private Boolean enabled;

	public ScriptDetailMainWord update(ScriptDetailMainWord target) {
		if (target.scriptDetailId != null) this.scriptDetailId = target.scriptDetailId;
		if (target.word != null) this.word = target.word;
		if (target.weight != null) this.weight = target.weight;
		if (target.updateUser != null) this.updateUser = target.updateUser;
		if (target.enabled != null) this.enabled = target.enabled;
		return this;
	}
}
