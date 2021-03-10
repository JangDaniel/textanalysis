package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_qa_product")
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id"})
public class Product {
	@Id
	private Long id;
	private Long insuranceType;
	private String code;
	private String name;
	private String modelCode;
	private Boolean ioType;
	private Boolean directType;

	@Transient
	@With
	private List<ScriptCriterion> scriptCriteria;
}
