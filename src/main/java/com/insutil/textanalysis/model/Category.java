package com.insutil.textanalysis.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "t_qa_product_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Category {
	@Id
	private Long id;
	private Long insuranceType;
	private String name;
	private String description;
	private Long parentId;

	@Transient
	@With
	private Code incuranceTypeCode;
}
