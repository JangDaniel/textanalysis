package com.insutil.textanalysis.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Category {
	@Id
	private Long id;
	private Long insuranceType;
	private String name;
	private String description;
	private Long parentId;
}
